package com.example.szs.controller;

import com.example.szs.model.dto.LHResponseDTO;
import com.example.szs.model.dto.LargeHoldingsDetailDTO;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.model.queryDSLSearch.LargeHoldingsDetailSearchCondition;
import com.example.szs.repository.stock.LargeHoldingsDetailRepositoryCustom;
import com.example.szs.service.stock.ExecOwnershipService;
import com.example.szs.service.stock.LargeHoldingsService;
import com.example.szs.utils.Response.ResUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final LargeHoldingsDetailRepositoryCustom largeHoldingsDetailRepositoryCustom;

    @GetMapping("/update")
    public Map<String, Object> update(@RequestParam boolean isExec) {
        if (isExec) {
            execOwnershipService.insertData();
        } else {
            largeHoldingsService.insertData();
        }
        return ResUtil.makeResponse("", ResStatus.SUCCESS);
    }

    @GetMapping("/search/large-holdings")
    public Page<LargeHoldingsDetailDTO> searchLargeHoldingsDetail(LargeHoldingsDetailSearchCondition condition, Pageable pageable) {
        return largeHoldingsDetailRepositoryCustom.searchPage(condition, pageable);
    }
}
