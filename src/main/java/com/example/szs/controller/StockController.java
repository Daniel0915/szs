package com.example.szs.controller;

import com.example.szs.model.dto.LHResponseDTO;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.service.stock.ExecOwnershipService;
import com.example.szs.service.stock.LargeHoldingsService;
import com.example.szs.utils.Response.ResUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {
    private final LargeHoldingsService largeHoldingsService;
    private final ExecOwnershipService execOwnershipService;

    @GetMapping("/update")
    public Map<String, Object> update(@RequestParam boolean isExec) {
        if (isExec) {
            execOwnershipService.insertData();
        } else {
            largeHoldingsService.insertData();
        }
        return ResUtil.makeResponse("", ResStatus.SUCCESS);
    }
}
