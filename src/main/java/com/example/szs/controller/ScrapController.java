package com.example.szs.controller;


import com.example.szs.dto.MemberReq;
import com.example.szs.service.MemberService;
import com.example.szs.service.ScrapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

// TODO : "szs" prefix 필요 (ex : /szs/signup)
@RestController
@RequestMapping("/szs/scrap")
@RequiredArgsConstructor
@Slf4j
public class ScrapController {
    private final ScrapService scrapService;

    @GetMapping
    public Map<String, Object> scrap(@RequestParam(required = false) Long seq) throws ParseException {
        scrapService.insertScrapInfo(seq);
        return new HashMap<>();
    }
}
