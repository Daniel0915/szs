package com.example.szs.insideTrade.domain;

import com.example.szs.insideTrade.application.dto.LargeHoldingsDetailDTO;
import com.example.szs.insideTrade.presentation.dto.request.LargeHoldingsDetailSearchConditionReqDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LargeHoldingsDetailRepo {
    List<LargeHoldingsDetail> saveAll(List<LargeHoldingsDetail> entities);
    void insertNativeBatch(List<LargeHoldingsDetail> entities, int batchSize);
    Page<LargeHoldingsDetail> searchPage(LargeHoldingsDetailSearchConditionReqDTO condition, Pageable pageable);
    List<LargeHoldingsDetailDTO.MonthlyCountDTO> getLargeHoldingsMonthlyTradeCnt(String corpCode, boolean isSell);
    List<LargeHoldingsDetail> getLargeHoldingsDetailListBy(LargeHoldingsDetailSearchConditionReqDTO condition);
    List<LargeHoldingsDetailDTO.TopStockDetailDTO> getTopStockDetail(LargeHoldingsDetailSearchConditionReqDTO condition);
}
