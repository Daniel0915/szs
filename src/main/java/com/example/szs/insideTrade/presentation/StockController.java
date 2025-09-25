package com.example.szs.insideTrade.presentation;

import com.example.szs.insideTrade.application.ExecOwnershipService;
import com.example.szs.insideTrade.application.LargeHoldingsService;
import com.example.szs.insideTrade.domain.LargeHoldingsDetailRepo;
import com.example.szs.insideTrade.domain.LargeHoldingsRepo;
import com.example.szs.insideTrade.domain.LargeHoldingsStkrtRepo;
import com.example.szs.insideTrade.presentation.dto.request.LargeHoldingsDetailSearchConditionReqDTO;
import com.example.szs.insideTrade.presentation.dto.response.PageResDTO;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.module.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {
    private final LargeHoldingsService largeHoldingsService;
    private final ApiResponse          apiResponse;

    @GetMapping("/search/large-holdings")
    public ResponseEntity<?> searchLargeHoldingsDetail(LargeHoldingsDetailSearchConditionReqDTO condition, Pageable pageable) {
        try {
            PageResDTO res = largeHoldingsService.getSearchPageLargeHoldingsDetail(condition, pageable);
            return apiResponse.makeResponse(ResStatus.SUCCESS, res);
        } catch (Exception e) {
            log.error("예상하지 못한 예외 에러 발생 : ", e);
            return apiResponse.makeResponse(ResStatus.ERROR);
        }
    }
//
//    @GetMapping("/search/exec-ownership")
//    public ResponseEntity<?> searchExecOwnershipDetail(ExecOwnershipDetailSearchCondition condition, Pageable pageable) {
//        try {
//            return execOwnershipService.getSearchPageExecOwnershipDetail(condition, pageable);
//        } catch (Exception e) {
//            log.error("예상하지 못한 예외 에러 발생 : ", e);
//            return apiResponse.makeResponse(ResStatus.ERROR);
//        }
//    }
//
//    @PostMapping("/update-scraping")
//    public ResponseEntity<?> updateScraping(@RequestBody List<LargeHoldingsDTO> largeHoldingsDTOList) {
//        // TODO : header 체크 필요
//        return largeHoldingsService.updateScraping(largeHoldingsDTOList);
//    }
//
//    @GetMapping("/large-holdings-stock-ratio")
//    public ResponseEntity<?> getLargeHoldingsStockRatio(LargeHoldingStkrtSearchCondition condition) {
//        if (condition.getCorpCode() == null) {
//            Map<String, Object> params = new HashMap<>() {{put(LargeHoldingStkrtSearchCondition.Fields.corpCode, condition.getCorpCode());}};
//            log.error(ErrorMsgUtil.paramErrorMessage(params));
//            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
//        }
//
//        try {
//            return largeHoldingsService.getLargeHoldingsStockRatio(condition);
//        } catch (Exception e) {
//            log.error("예상하지 못한 예외 에러 발생 : ", e);
//            return apiResponse.makeResponse(ResStatus.ERROR);
//        }
//    }
//
//    @GetMapping("/exec-ownership-ratio")
//    public ResponseEntity<?> getExecOwnershipRatio(@RequestParam(required = false) String corpCode) {
//        if (!StringUtils.hasText(corpCode)) {
//            Map<String, Object> params = new HashMap<>() {{put(CORP_CODE, corpCode);}};
//            log.error(ErrorMsgUtil.paramErrorMessage(params));
//            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
//        }
//
//        try {
//            return execOwnershipService.getRatio(corpCode);
//        } catch (Exception e) {
//            log.error("예상하지 못한 예외 에러 발생 : ", e);
//            return apiResponse.makeResponse(ResStatus.ERROR);
//        }
//    }
//
//    @GetMapping("/large-holdings-monthly-trade-cnt")
//    public ResponseEntity<?> getLargeHoldingsMonthlyTradeCnt(@RequestParam(required = false) String corpCode) {
//        if (!StringUtils.hasText(corpCode)) {
//            Map<String, Object> params = new HashMap<>() {{put(CORP_CODE, corpCode);}};
//            log.error(ErrorMsgUtil.paramErrorMessage(params));
//            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
//        }
//
//        try {
//            return largeHoldingsService.getLargeHoldingsMonthlyTradeCnt(corpCode);
//        } catch (Exception e) {
//            log.error("예상하지 못한 예외 에러 발생 : ", e);
//            return apiResponse.makeResponse(ResStatus.ERROR);
//        }
//    }
//
//    @GetMapping("/exec-ownership-monthly-trade-cnt")
//    public ResponseEntity<?> getExecOwnershipMonthlyTradeCnt(@RequestParam(required = false) String corpCode) {
//        if (!StringUtils.hasText(corpCode)) {
//            Map<String, Object> params = new HashMap<>() {{put(CORP_CODE, corpCode);}};
//            log.error(ErrorMsgUtil.paramErrorMessage(params));
//            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
//        }
//
//        try {
//            return execOwnershipService.getMonthlyTradeCnt(corpCode);
//        } catch (Exception e) {
//            log.error("예상하지 못한 예외 에러 발생 : ", e);
//            return apiResponse.makeResponse(ResStatus.ERROR);
//        }
//    }
//
//    @GetMapping("/large-holdings-top-5")
//    public ResponseEntity<?> getLargeHoldingsStockRatioTop5(@RequestParam(required = false) String corpCode) {
//        if (!StringUtils.hasText(corpCode)) {
//            Map<String, Object> params = new HashMap<>() {{put(CORP_CODE, corpCode);}};
//            log.error(ErrorMsgUtil.paramErrorMessage(params));
//            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
//        }
//
//        try {
//            return largeHoldingsService.getLargeHoldingsStockRatioTop5(corpCode);
//        } catch (Exception e) {
//            log.error("예상하지 못한 예외 에러 발생 : ", e);
//            return apiResponse.makeResponse(ResStatus.ERROR);
//        }
//    }
//
//    @GetMapping("/exec-ownership-top-5")
//    public ResponseEntity<?> getStockCntTop5(@RequestParam(required = false) String corpCode) {
//        if (!StringUtils.hasText(corpCode)) {
//            Map<String, Object> params = new HashMap<>() {{put(CORP_CODE, corpCode);}};
//            log.error(ErrorMsgUtil.paramErrorMessage(params));
//            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
//        }
//
//        try {
//            return execOwnershipService.getStockCntTop5(corpCode);
//        } catch (Exception e) {
//            log.error("예상하지 못한 예외 에러 발생 : ", e);
//            return apiResponse.makeResponse(ResStatus.ERROR);
//        }
//    }
//
//    @GetMapping("/exec-ownership-trade-list")
//    public ResponseEntity<?> getExecOwnershipTradeList(@RequestParam(required = false) String corpCode,
//                                                       @RequestParam(required = false) String execOwnershipName) {
//        if (!StringUtils.hasText(corpCode) || !StringUtils.hasText(execOwnershipName)) {
//            Map<String, Object> params = new HashMap<>() {{
//                put(CORP_CODE, corpCode);
//                put(EXEC_OWNERSHIP_NAME, execOwnershipName);
//            }};
//            log.error(ErrorMsgUtil.paramErrorMessage(params));
//            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
//        }
//
//        try {
//            return execOwnershipService.getExecOwnershipTradeList(ExecOwnershipDetailSearchCondition.builder()
//                                                                                                    .corpCodeEq(corpCode)
//                                                                                                    .execOwnershipNameEq(execOwnershipName)
//                                                                                                    .orderColumn(ExecOwnershipDetail.Fields.tradeDt)
//                                                                                                    .isDescending(false)
//                                                                                                    .build());
//        } catch (Exception e) {
//            log.error("예상하지 못한 예외 에러 발생 : ", e);
//            return apiResponse.makeResponse(ResStatus.ERROR);
//        }
//    }
//
//    @GetMapping("/large-holdings-trade-history")
//    public ResponseEntity<?> getLargeHoldingsTradeHistory(@RequestParam(required = false) String corpCode, @RequestParam(required = false) String largeHoldingsName) {
//        if (!StringUtils.hasText(corpCode)|| !StringUtils.hasText(largeHoldingsName)) {
//            Map<String, Object> params = new HashMap<>() {{
//                put(CORP_CODE, corpCode);
//                put(LARGE_HOLDINGS_NAME, largeHoldingsName);
//            }};
//            log.error(ErrorMsgUtil.paramErrorMessage(params));
//            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
//        }
//
//        try {
//            return largeHoldingsService.getLargeHoldingsTradeDtBy(corpCode, largeHoldingsName);
//        } catch (Exception e) {
//            log.error("예상하지 못한 예외 에러 발생 : ", e);
//            return apiResponse.makeResponse(ResStatus.ERROR);
//        }
//    }
//
//    @GetMapping("/corp-info-all")
//    public ResponseEntity<?> getAllCorpInfoDTOList() {
//        try {
//            return corpInfoService.getAllCorpInfoDTOList();
//        } catch (Exception e) {
//            log.error("예상하지 못한 예외 에러 발생 : ", e);
//            return apiResponse.makeResponse(ResStatus.ERROR);
//        }
//    }
//
//
//    @GetMapping("/trade-top-5")
//    public ResponseEntity<?> getTop5StockTrade(@RequestParam(required = false) String tradeDtGoe, @RequestParam(required = false) String tradeDtLoe) {
//        if (!StringUtils.hasText(tradeDtGoe)|| !StringUtils.hasText(tradeDtLoe)) {
//            Map<String, Object> params = new HashMap<>() {{
//                put(TRADE_DT_GOE, tradeDtGoe);
//                put(TRADE_DT_LOE, tradeDtLoe);
//            }};
//            log.error(ErrorMsgUtil.paramErrorMessage(params));
//            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
//        }
//
//        try {
//            Map<String, Object> result = new HashMap<>(){{
//                put(ShareDisclosure.LARGE_HOLDINGS.getCode(), largeHoldingsService.getTop5StockTrade(tradeDtGoe, tradeDtLoe));
//                put(ShareDisclosure.EXEC_OWNERSHIP.getCode(), execOwnershipService.getTop5StockTrade(tradeDtGoe, tradeDtLoe));
//            }};
//
//            return apiResponse.makeResponse(ResStatus.SUCCESS, result);
//        } catch (Exception e) {
//            log.error("예상하지 못한 예외 에러 발생 : ", e);
//            return apiResponse.makeResponse(ResStatus.ERROR);
//        }
//    }
//
//    @GetMapping("/trade-total")
//    public ResponseEntity<?> getTopStockTradeTotal(@RequestParam(required = false) String tradeDtGoe,
//                                                   @RequestParam(required = false) String tradeDtLoe,
//                                                   @RequestParam(required = false, defaultValue = "ALL") SellOrBuyType sellOrBuyType) {
//        if (!StringUtils.hasText(tradeDtGoe)|| !StringUtils.hasText(tradeDtLoe)) {
//            Map<String, Object> params = new HashMap<>() {{
//                put(TRADE_DT_GOE, tradeDtGoe);
//                put(TRADE_DT_LOE, tradeDtLoe);
//                put(SELL_OR_BUY_TYPE, sellOrBuyType);
//            }};
//            log.error(ErrorMsgUtil.paramErrorMessage(params));
//            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
//        }
//
//        try {
//            return largeHoldingsService.getTopStockTradeTotal(tradeDtGoe, tradeDtLoe, sellOrBuyType);
//        } catch (Exception e) {
//            log.error("예상하지 못한 예외 에러 발생 : ", e);
//            return apiResponse.makeResponse(ResStatus.ERROR);
//        }
//    }
}
