package com.example.szs.insideTrade.domain;

import java.util.List;

public interface LargeHoldingsDetailRepo {
    List<LargeHoldingsDetail> saveAll(List<LargeHoldingsDetail> entities);
    void insertNativeBatch(List<LargeHoldingsDetail> entities, int batchSize);
}
