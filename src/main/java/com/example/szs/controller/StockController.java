package com.example.szs.controller;

import com.example.szs.model.dto.LargeHoldingsDTO;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.model.queryDSLSearch.LargeHoldingStkrtSearchCondition;
import com.example.szs.model.queryDSLSearch.LargeHoldingsDetailSearchCondition;
import com.example.szs.module.ApiResponse;
import com.example.szs.repository.stock.LargeHoldingsDetailRepositoryCustom;
import com.example.szs.repository.stock.LargeHoldingsStkrtRepositoryCustom;
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
    private final LargeHoldingsDetailRepositoryCustom largeHoldingsDetailRepositoryCustom;
    private final ApiResponse apiResponse;

    // TODO : 테스트 코드
    private final LargeHoldingsStkrtRepositoryCustom largeHoldingsStkrtRepositoryCustom;

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
        if (condition.getCorpCodeEq() == null) {
            Map<String, Object> params = new HashMap<>() {{put(LargeHoldingStkrtSearchCondition.Fields.corpCodeEq, condition.getCorpCodeEq());}};
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
}
