package com.example.szs.insideTrade.domain;

import java.util.List;

public interface ExecOwnershipDetailRepo {
    void insertNativeBatch(List<ExecOwnershipDetail> entities, int batchSize);
}
