package com.example.szs.controller;


import com.example.szs.dto.MemberReq;
import com.example.szs.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// TODO : "szs" prefix 필요 (ex : /szs/signup)
@RestController
@RequestMapping("/szs/signup")
@RequiredArgsConstructor
@Slf4j
public class SignUpController {
    private final MemberService memberService;

    @PostMapping
    public Map<String, Object> join(@RequestBody MemberReq memberReq) {
        Map<String, Object> result = new HashMap<>();
        try {
            String userId = memberService.join(memberReq);
            result.put("userId", userId);
        } catch (Exception e) {
            // TODO : 응답별 상태 코드 정리
            e.printStackTrace();
            return result;
        }
        return result;
    }
}
