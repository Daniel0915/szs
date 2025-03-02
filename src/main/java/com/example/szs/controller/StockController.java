package com.example.szs.controller;

import com.example.szs.model.dto.corpInfo.CorpInfoDTO;
import com.example.szs.model.dto.largeHoldings.LargeHoldingsDTO;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.model.queryDSLSearch.LargeHoldingStkrtSearchCondition;
import com.example.szs.model.queryDSLSearch.LargeHoldingsDetailSearchCondition;
import com.example.szs.module.ApiResponse;
import com.example.szs.repository.stock.CorpInfoRepositoryCustom;
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

import java.util.*;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {
    private final LargeHoldingsService largeHoldingsService;
    private final ExecOwnershipService execOwnershipService;
    private final CorpInfoService corpInfoService;
    private final ApiResponse apiResponse;
    private final CorpInfoRepositoryCustom corpInfoRepositoryCustom;

    @GetMapping("/update")
    public Map<String, Object> update() {
        List<String> corpCodeList = Arrays.asList("00918444","00375302","00126229","00144155","01664948","00860332","00159023",
                "01133217","00126362","00126371","00164645","00145109","00989619","00120021",
                "00356361","00106641","00126566","00159616","00760971","00401731","01205851",
                "01350869","00266961","00547583","00149655","00382199","00126380","00164779",
                "00181712","01652129","01596425","01204056","00111704","01390344","00877059",
                "01160363","00155276","01515323","00258801","00631518","00688996","00421045",
                "00149646","00102858","00413046","00199252","00126256","00126478","00155319",
                "00164788","00164830");
        for (String corpCode : corpCodeList) {
            largeHoldingsService.insertData(corpCode);
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
}
