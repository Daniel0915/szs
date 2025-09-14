package com.example.szs.insideTrade.domain;

import com.example.szs.insideTrade.infrastructure.db.queryDSL.LargeHoldingsSearchCondition;

import java.util.List;
import java.util.Optional;

public interface LargeHoldingsRepo {
    Optional<LargeHoldings> findLatestRecordBy(LargeHoldingsSearchCondition searchCondition);
    List<LargeHoldings> saveAll(List<LargeHoldings> largeHoldings);
    void insertNativeBatch(List<LargeHoldings> largeHoldings, int batchSize) throws Exception;
}
