package com.example.szs.controller;


import com.example.szs.model.auth.AuthMember;
import com.example.szs.model.dto.MemberReq;
import com.example.szs.service.MemberService;
import com.example.szs.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// TODO : "szs" prefix 필요 (ex : /szs/signup)
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final MemberService memberService;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody MemberReq memberReq) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(memberReq.getUserId(), memberReq.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthMember userDetails = (AuthMember) authentication.getPrincipal();

        log.info("Token requested for user :{}", authentication.getAuthorities());
        String token = authService.generateToken(authentication);
        return ResponseEntity.ok(token);
    }
}
