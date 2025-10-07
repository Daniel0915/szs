package com.example.szs.insideTrade.domain;

import com.example.szs.insideTrade.application.dto.ExecOwnershipDetailDTO;
import com.example.szs.insideTrade.presentation.dto.request.ExecOwnershipDetailSearchConditionReqDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExecOwnershipDetailRepo {
    void insertNativeBatch(List<ExecOwnershipDetail> entities, int batchSize);
    Page<ExecOwnershipDetail> searchPage(ExecOwnershipDetailSearchConditionReqDTO condition, Pageable pageable);
    List<ExecOwnershipDetail> getExecOwnershipDetailList(ExecOwnershipDetailSearchConditionReqDTO condition);
    List<ExecOwnershipDetailDTO.MonthlyCountDTO> getMonthlyTradeCnt(String corpCode, boolean isSell);
    List<ExecOwnershipDetailDTO.TopStockDetailDTO> getTopStockDetail(ExecOwnershipDetailSearchConditionReqDTO condition);
}
