package com.example.szs.controller;


import com.example.szs.module.jwt.JwtTokenProvider;
import com.example.szs.service.RefundService;
import com.example.szs.service.ScrapService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

// TODO : "szs" prefix 필요 (ex : /szs/signup)
@RestController
@RequestMapping("/refund")
@RequiredArgsConstructor
@Slf4j
public class RefundController {
    private final RefundService refundService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public Map<String, Object> refund(HttpServletRequest request, @RequestParam(required = false, defaultValue = "-1") int year) throws ParseException {
        String token = jwtTokenProvider.getTokenFromRequest(request);
        Long memberSeq = jwtTokenProvider.getClaimMemberSeq(token);
        return refundService.getFinalTax(memberSeq, year);
    }
}
