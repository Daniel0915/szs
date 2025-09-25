package com.example.szs.insideTrade.domain;

import com.example.szs.insideTrade.presentation.dto.request.LargeHoldingsDetailSearchConditionReqDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LargeHoldingsDetailRepo {
    List<LargeHoldingsDetail> saveAll(List<LargeHoldingsDetail> entities);
    void insertNativeBatch(List<LargeHoldingsDetail> entities, int batchSize);
    Page<LargeHoldingsDetail> searchPage(LargeHoldingsDetailSearchConditionReqDTO condition, Pageable pageable);
}
