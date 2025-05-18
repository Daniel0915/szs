package com.example.szs.controller;


import com.example.szs.model.dto.MemberReq;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.service.MemberService;
import com.example.szs.utils.Response.ApiResponse;
import com.example.szs.utils.Response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
@Slf4j
public class SignUpController {
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<?> join(@RequestBody MemberReq memberReq) {
        try {
            memberService.join(memberReq);
            return ResponseEntity.ok(ApiResponse.success("", ResStatus.SUCCESS));
        } catch (Exception e) {
            log.error("error message : {}", e.getMessage());
            return ResponseEntity.status    (HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body      (ErrorResponse.of(ResStatus.ERROR));
        }
    }
}
