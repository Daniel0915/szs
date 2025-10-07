package com.example.szs.insideTrade.domain;

import com.example.szs.insideTrade.presentation.dto.request.LargeHoldingStkrtSearchConditionReqDTO;

import java.util.List;

public interface LargeHoldingsStkrtRepo {
    List<LargeHoldingsStkrt> saveAll(List<LargeHoldingsStkrt> entities);
    void insertNativeBatch(List<LargeHoldingsStkrt> entities, int batchSize);
    List<LargeHoldingsStkrt> getLargeHoldingsStockRatio(LargeHoldingStkrtSearchConditionReqDTO condition);
}
