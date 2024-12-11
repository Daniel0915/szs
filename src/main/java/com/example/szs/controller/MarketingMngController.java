package com.example.szs.controller;

import com.example.szs.model.dto.MarketingMngDTO;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.service.marketing.MarketingMngService;
import com.example.szs.utils.Response.ResUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/marketing-mng")
@RequiredArgsConstructor
@Slf4j
public class MarketingMngController {
    private final MarketingMngService marketingMngService;

    @GetMapping("/select")
    public Map<String, Object> getMarketingMng(@RequestParam int seq) {
        return ResUtil.makeResponse(marketingMngService.getMarketingMng(seq), ResStatus.SUCCESS);
    }


    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody MarketingMngDTO marketingMngDTO) {
        marketingMngService.save(marketingMngDTO);
        return ResUtil.makeResponse("", ResStatus.SUCCESS);
    }
}
