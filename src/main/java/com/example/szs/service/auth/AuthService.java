package com.example.szs.service.auth;

import com.example.szs.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    // TODO : passwordEncoder, jwtEncoder 추가
    private final MemberRepository memberRepository;
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        String scope = authentication.getAuthorities()
                                     .stream()
                                     .map(GrantedAuthority::getAuthority)
                                     .collect(Collectors.joining(" "));
        // TODO : rsa 추가해서 반환해주기

        JwtClaimsSet claims = JwtClaimsSet.builder()
                                          .issuer("self")
                                          .issuedAt(now)
                                          .expiresAt(now.plus(10, ChronoUnit.HOURS))
                                          .subject(authentication.getName())
                                          .claim("scope", scope)
                                          .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // TODO : 테스트 중 삭제
//        String token = String.format("User: %s, Scope: %s, IssuedAt: %s, ExpiresAt: %s",
//                authentication.getName(),
//                scope,
//                now.toString(),
//                now.plus(10, ChronoUnit.HOURS).toString());
//
//        return token;

    }


}
