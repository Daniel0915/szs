package com.example.szs.module.jwt;

import com.example.szs.exception.CustomException;
import com.example.szs.model.eNum.ResStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    @Value("${ACCESS_TOKEN_EXPIRE_TIME}")
    private String accessTokenExpireTime;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public String generateToken(Authentication authentication, Long memberSeq, String userId) throws Exception {
        Instant now = Instant.now();
        String scope = "USER";
        JwtClaimsSet claims = JwtClaimsSet.builder()
                                          .issuer("self")
                                          .issuedAt(now)
                                          .expiresAt(now.plus(Long.valueOf(accessTokenExpireTime), ChronoUnit.MILLIS)) // 현재 시각에서 6시간 후 만료
                                          .subject(authentication.getName())
                                          .claim("scope", scope)
                                          .claim("memberSeq", memberSeq)
                                          .claim("userId", userId)
                                          .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Long getClaimMemberSeq(String token) {
        return jwtDecoder.decode(token).getClaim("memberSeq");
    }

    public String getClaimUserId(String token) {
        return jwtDecoder.decode(token).getClaim("userId");
    }
}
