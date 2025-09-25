package com.example.szs.insideTrade.domain;

import com.example.szs.model.dto.execOwnership.ExecOwnershipDetailDTO;
import com.example.szs.model.queryDSLSearch.ExecOwnershipDetailSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExecOwnershipDetailRepo {
    void insertNativeBatch(List<ExecOwnershipDetail> entities, int batchSize);
    Page<ExecOwnershipDetail> searchPage(ExecOwnershipDetailSearchCondition condition, Pageable pageable);
    List<ExecOwnershipDetail> getExecOwnershipDetailList(ExecOwnershipDetailSearchCondition condition);
    List<ExecOwnershipDetailDTO.MonthlyCountDTO> getMonthlyTradeCnt(String corpCode, boolean isSell);
    List<ExecOwnershipDetailDTO.TopStockDetailDTO> getTopStockDetail(ExecOwnershipDetailSearchCondition condition);
}
