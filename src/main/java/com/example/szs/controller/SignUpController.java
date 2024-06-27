package com.example.szs.controller;


import com.example.szs.exception.CustomException;
import com.example.szs.model.dto.MemberReq;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.service.MemberService;
import com.example.szs.utils.Response.ResUtil;
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
@RequestMapping("${apiPrefix}/signup")
@RequiredArgsConstructor
@Slf4j
public class SignUpController {
    private final MemberService memberService;

    @PostMapping
    public Map<String, Object> join(@RequestBody MemberReq memberReq) {
        try {
            Map<String, Object> result = new HashMap<>();
            String userId = memberService.join(memberReq);
            result.put("userId", userId);
            return ResUtil.makeResponse(result, ResStatus.SUCCESS);
        } catch (Exception e) {
            return ResUtil.makeErrorResponse(e);
        }
    }
}
