package com.example.szs.insideTrade.domain;

import com.example.szs.insideTrade.application.dto.ExecOwnershipDTO;
import com.example.szs.insideTrade.presentation.dto.request.ExecOwnershipSearchCondition;

import java.util.List;
import java.util.Optional;

public interface ExecOwnershipRepo {
    Optional<ExecOwnershipDTO> findLatestRecordBy(ExecOwnershipSearchCondition condition);
    List<ExecOwnership> getExecOwnershipOrderSpStockLmpCnt(String corpCode);
    void insertNativeBatch(List<ExecOwnership> execOwnerships, int batchSize);

}
