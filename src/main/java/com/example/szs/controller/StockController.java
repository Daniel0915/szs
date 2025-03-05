package com.example.szs.controller;

import com.example.szs.model.dto.largeHoldings.LargeHoldingsDTO;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.model.eNum.stock.SellOrBuyType;
import com.example.szs.model.queryDSLSearch.ExecOwnershipDetailSearchCondition;
import com.example.szs.model.queryDSLSearch.ExecOwnershipSearchCondition;
import com.example.szs.model.queryDSLSearch.LargeHoldingStkrtSearchCondition;
import com.example.szs.model.queryDSLSearch.LargeHoldingsDetailSearchCondition;
import com.example.szs.module.ApiResponse;
import com.example.szs.service.stock.CorpInfoService;
import com.example.szs.service.stock.ExecOwnershipService;
import com.example.szs.service.stock.LargeHoldingsService;
import com.example.szs.utils.Response.ResUtil;
import com.example.szs.utils.error.ErrorMsgUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
    private final CorpInfoService corpInfoService;
    private final ApiResponse apiResponse;

    @GetMapping("/update/large-holdings")
    public Map<String, Object> updateLargeHoldings() throws InterruptedException {
        List<String> corpCodeList = Arrays.asList("00164645","00145109","00989619","00120021");
        for (String corpCode : corpCodeList) {
            Thread.sleep(3000);
            largeHoldingsService.insertData(corpCode);
        }

        for (String corpCode : corpCodeList) {
            Thread.sleep(3000);
            execOwnershipService.insertData(corpCode);
        }

        return ResUtil.makeResponse("", ResStatus.SUCCESS);
    }

    @GetMapping("/update/exec-ownership")
    public Map<String, Object> updateExecOwnership() throws InterruptedException {
        List<String> corpCodeList = Arrays.asList("00159023");
        for (String corpCode : corpCodeList) {
            Thread.sleep(3000);
            execOwnershipService.insertData(corpCode);
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

    @GetMapping("/search/exec-ownership")
    public ResponseEntity<?> searchExecOwnershipDetail(ExecOwnershipDetailSearchCondition condition, Pageable pageable) {
        try {
            return execOwnershipService.getSearchPageExecOwnershipDetail(condition, pageable);
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
    public ResponseEntity<?> getLargeHoldingsMonthlyTradeCnt(String corpCode) {
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

    @GetMapping("/large-holdings-top-5")
    public ResponseEntity<?> getLargeHoldingsStockRatioTop5(String corpCode) {
        if (corpCode == null) {
            Map<String, Object> params = new HashMap<>() {{put("corpCode", corpCode);}};
            log.error(ErrorMsgUtil.paramErrorMessage(params));
            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
        }

        try {
            return largeHoldingsService.getLargeHoldingsStockRatioTop5(corpCode);
        } catch (Exception e) {
            log.error("예상하지 못한 예외 에러 발생 : ", e);
            return apiResponse.makeResponse(ResStatus.ERROR);
        }
    }

    @GetMapping("/exec-ownership-top-5")
    public ResponseEntity<?> getStockCntTop5(String corpCode) {
        if (corpCode == null) {
            Map<String, Object> params = new HashMap<>() {{put("corpCode", corpCode);}};
            log.error(ErrorMsgUtil.paramErrorMessage(params));
            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
        }

        try {
            return execOwnershipService.getStockCntTop5(corpCode);
        } catch (Exception e) {
            log.error("예상하지 못한 예외 에러 발생 : ", e);
            return apiResponse.makeResponse(ResStatus.ERROR);
        }
    }

    @GetMapping("/large-holdings-trade-history")
    public ResponseEntity<?> getLargeHoldingsTradeHistory(@RequestParam(required = false) String corpCode, @RequestParam(required = false) String largeHoldingsName) {
        if (!StringUtils.hasText(corpCode)|| !StringUtils.hasText(largeHoldingsName)) {
            Map<String, Object> params = new HashMap<>() {{
                put("corpCode", corpCode);
                put("largeHoldingsName", largeHoldingsName);
            }};
            log.error(ErrorMsgUtil.paramErrorMessage(params));
            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
        }

        try {
            return largeHoldingsService.getLargeHoldingsTradeDtBy(corpCode, largeHoldingsName);
        } catch (Exception e) {
            log.error("예상하지 못한 예외 에러 발생 : ", e);
            return apiResponse.makeResponse(ResStatus.ERROR);
        }
    }

    @GetMapping("/corp-info-all")
    public ResponseEntity<?> getAllCorpInfoDTOList() {
        try {
            return corpInfoService.getAllCorpInfoDTOList();
        } catch (Exception e) {
            log.error("예상하지 못한 예외 에러 발생 : ", e);
            return apiResponse.makeResponse(ResStatus.ERROR);
        }
    }

    @GetMapping("/trade-top-5")
    public ResponseEntity<?> getTop5StockTrade(@RequestParam(required = false) String tradeDtGoe, @RequestParam(required = false) String tradeDtLoe) {
        if (!StringUtils.hasText(tradeDtGoe)|| !StringUtils.hasText(tradeDtLoe)) {
            Map<String, Object> params = new HashMap<>() {{
                put("tradeDtGoe", tradeDtGoe);
                put("tradeDtLoe", tradeDtLoe);
            }};
            log.error(ErrorMsgUtil.paramErrorMessage(params));
            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
        }

        try {
            return largeHoldingsService.getTop5StockTrade(tradeDtGoe, tradeDtLoe);
        } catch (Exception e) {
            log.error("예상하지 못한 예외 에러 발생 : ", e);
            return apiResponse.makeResponse(ResStatus.ERROR);
        }
    }

    @GetMapping("/trade-total")
    public ResponseEntity<?> getTopStockTradeTotal(@RequestParam(required = false) String tradeDtGoe,
                                                   @RequestParam(required = false) String tradeDtLoe,
                                                   @RequestParam(required = false, defaultValue = "ALL") SellOrBuyType sellOrBuyType) {
        if (!StringUtils.hasText(tradeDtGoe)|| !StringUtils.hasText(tradeDtLoe)) {
            Map<String, Object> params = new HashMap<>() {{
                put("tradeDtGoe", tradeDtGoe);
                put("tradeDtLoe", tradeDtLoe);
                put("sellOrBuyType", sellOrBuyType);
            }};
            log.error(ErrorMsgUtil.paramErrorMessage(params));
            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
        }

        try {
            return largeHoldingsService.getTopStockTradeTotal(tradeDtGoe, tradeDtLoe, sellOrBuyType);
        } catch (Exception e) {
            log.error("예상하지 못한 예외 에러 발생 : ", e);
            return apiResponse.makeResponse(ResStatus.ERROR);
        }
    }
}
