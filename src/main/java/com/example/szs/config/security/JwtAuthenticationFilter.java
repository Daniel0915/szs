//package com.example.szs.config.security;
//
//import com.example.szs.model.eNum.ResStatus;
//import com.example.szs.module.jwt.JwtTokenProvider;
//import com.example.szs.service.auth.JpaMemberDetailService;
//import com.example.szs.utils.Response.ResUtil;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//
//@Slf4j
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtTokenProvider jwtTokenProvider;
//    private final String[] permitAllArray;
//    private final String[] hasAuthArray;
//    private final JpaMemberDetailService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        if (isPermitAll(request)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = jwtTokenProvider.getTokenFromRequest(request);
//
//        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
//            String userId = jwtTokenProvider.getClaimUserId(token);
//
//            UserDetails userDetails;
//            try {
//                userDetails = userDetailsService.loadUserByUsername(userId);
//            } catch (Exception e) {
//                ResUtil.makeForbiddenResponse(response, ResStatus.ACCESS_KEY_ERROR);
//                return;
//            }
//
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        ResUtil.makeForbiddenResponse(response, ResStatus.ACCESS_KEY_EXPIRE);
//    }
//
//    private boolean isPermitAll(HttpServletRequest request) {
//        String url = request.getRequestURI();
//        AntPathMatcher antPathMatcher = new AntPathMatcher();
//        for (String pattern : permitAllArray) {
//            if (antPathMatcher.match(pattern, url)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean isHasAuth(HttpServletRequest request) {
//        String url = request.getRequestURI();
//        AntPathMatcher antPathMatcher = new AntPathMatcher();
//        for (String pattern : hasAuthArray) {
//            if (antPathMatcher.match(pattern, url)) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
