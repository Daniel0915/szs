package com.example.szs.config.security;

import com.example.szs.module.jwt.JwtTokenProvider;
import com.example.szs.service.auth.JpaMemberDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final String[] permitAllArray;
    private final JpaMemberDetailService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isPermitAll(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.getTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            try {
                Long memberSeq = jwtTokenProvider.getClaimMemberSeq(token);
                String userId = jwtTokenProvider.getClaimUserId(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPermitAll(HttpServletRequest request) {
        String url = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String pattern : permitAllArray) {
            if (antPathMatcher.match(pattern, url)) {
                return true;
            }
        }
        return false;
    }
}
