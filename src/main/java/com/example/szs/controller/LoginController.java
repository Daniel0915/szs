package com.example.szs.controller;


import com.example.szs.model.auth.AuthMember;
import com.example.szs.model.dto.LoginReq;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.module.jwt.JwtTokenProvider;
import com.example.szs.utils.Response.ResUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;


    @PostMapping
    public Map<String, Object> login(@RequestBody LoginReq loginReq) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReq.getUserId(), loginReq.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            AuthMember userDetails = (AuthMember) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(authentication, userDetails.getMemberSeq(), userDetails.getUsername());

            Map<String, String> result = new HashMap<>();
            result.put("accessToken", token);
            return ResUtil.makeResponse(result, ResStatus.SUCCESS);
        } catch (Exception e) {
            return ResUtil.makeErrorResponse(e);
        }
    }
}
