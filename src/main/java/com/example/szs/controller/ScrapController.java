package com.example.szs.controller;


import com.example.szs.model.auth.AuthMember;
import com.example.szs.module.jwt.JwtTokenProvider;
import com.example.szs.service.ScrapService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

// TODO : "szs" prefix 필요 (ex : /szs/signup)
@RestController
@RequestMapping("/scrap")
@RequiredArgsConstructor
@Slf4j
public class ScrapController {
    private final ScrapService scrapService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public Map<String, Object> scrap(HttpServletRequest request) throws ParseException {
        String token = jwtTokenProvider.getTokenFromRequest(request);
        Long memberSeq = jwtTokenProvider.getClaimMemberSeq(token);
        scrapService.insertScrapInfo(memberSeq);
        return new HashMap<>();
    }
}
