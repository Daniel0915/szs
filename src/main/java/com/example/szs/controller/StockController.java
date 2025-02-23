package com.example.szs.controller;

import com.example.szs.model.dto.largeHoldings.LargeHoldingsDTO;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.model.queryDSLSearch.LargeHoldingStkrtSearchCondition;
import com.example.szs.model.queryDSLSearch.LargeHoldingsDetailSearchCondition;
import com.example.szs.module.ApiResponse;
import com.example.szs.service.stock.ExecOwnershipService;
import com.example.szs.service.stock.LargeHoldingsService;
import com.example.szs.utils.Response.ResUtil;
import com.example.szs.utils.error.ErrorMsgUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {
    private final LargeHoldingsService largeHoldingsService;
    private final ExecOwnershipService execOwnershipService;
    private final ApiResponse apiResponse;

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
    public ResponseEntity<?> searchLargeHoldingsDetail(LargeHoldingsDetailSearchCondition condition, Pageable pageable) {
        try {
            return largeHoldingsService.getSearchPageLargeHoldingsDetail(condition, pageable);
        } catch (Exception e) {
            log.error("예상하지 못한 예외 에러 발생 : ", e);
            return apiResponse.makeResponse(ResStatus.ERROR);
        }
    }

    @PostMapping("/update-scraping")
    public ResponseEntity<?> updateScraping(@RequestBody List<LargeHoldingsDTO> largeHoldingsDTOList) {
        // TODO : header 체크 필요
        return largeHoldingsService.updateScraping(largeHoldingsDTOList);
    }

    @GetMapping("/large-holdings-stock-ratio")
    public ResponseEntity<?> getLargeHoldingsStockRatio(LargeHoldingStkrtSearchCondition condition) {
        if (condition.getCorpCode() == null) {
            Map<String, Object> params = new HashMap<>() {{put(LargeHoldingStkrtSearchCondition.Fields.corpCode, condition.getCorpCode());}};
            log.error(ErrorMsgUtil.paramErrorMessage(params));
            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
        }

        try {
            return largeHoldingsService.getLargeHoldingsStockRatio(condition);
        } catch (Exception e) {
            log.error("예상하지 못한 예외 에러 발생 : ", e);
            return apiResponse.makeResponse(ResStatus.ERROR);
        }
    }

    @GetMapping("/large-holdings-monthly-trade-cnt")
    public ResponseEntity<?> getLargeHoldingsMonthlyTradeCnt(Long corpCode) {
        if (corpCode == null) {
            Map<String, Object> params = new HashMap<>() {{put("corpCode", corpCode);}};
            log.error(ErrorMsgUtil.paramErrorMessage(params));
            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
        }

        try {
            return largeHoldingsService.getLargeHoldingsMonthlyTradeCnt(corpCode);
        } catch (Exception e) {
            log.error("예상하지 못한 예외 에러 발생 : ", e);
            return apiResponse.makeResponse(ResStatus.ERROR);
        }
    }
}
