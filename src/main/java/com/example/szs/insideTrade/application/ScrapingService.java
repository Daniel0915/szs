package com.example.szs.insideTrade.application;

import com.example.szs.insideTrade.domain.ExecOwnership;
import com.example.szs.insideTrade.domain.ExecOwnershipDetail;
import com.example.szs.insideTrade.domain.ExecOwnershipDetailRepo;
import com.example.szs.insideTrade.domain.LargeHoldings;
import com.example.szs.insideTrade.domain.LargeHoldingsDetail;
import com.example.szs.insideTrade.domain.LargeHoldingsDetailRepo;
import com.example.szs.insideTrade.domain.LargeHoldingsStkrt;
import com.example.szs.insideTrade.domain.LargeHoldingsStkrtRepo;
import com.example.szs.insideTrade.infrastructure.client.Dart;
import com.example.szs.insideTrade.infrastructure.client.dto.ExecOwnershipDetailCrawlingDTO;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsDetailCrawlingDTO;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsStkrtCrawlingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapingService {
    private final Dart dart;
    private final LargeHoldingsDetailRepo largeHoldingsDetailRepo;
    private final LargeHoldingsStkrtRepo largeHoldingsStkrtRepo;
    private final ExecOwnershipDetailRepo execOwnershipDetailRepo;

    @Transactional
    public void updateLargeHoldingsScrapingData(List<LargeHoldings> insertList) {
        for (LargeHoldings entity : insertList) {
            saveLargeHoldingsDetailData(entity);
            saveLargeHoldingsStkrtData(entity);
        }
    }

    @Transactional
    public void updateExecOwnershipsScrapingData(List<ExecOwnership> insertList) {
        for (ExecOwnership entity : insertList) {
            saveExecOwnershipsDetailData(entity);
        }
    }

    private void saveExecOwnershipsDetailData(ExecOwnership entity) {
        List<ExecOwnershipDetailCrawlingDTO> detailList = dart.getExecOwnershipDetailCrawling(
                entity.getRceptNo(),
                entity.getCorpCode(),
                entity.getCorpName(),
                entity.getRepror(),
                entity.getIsuExctvRgistAt(),
                entity.getIsuExctvOfcps(),
                entity.getIsuMainShrholdr()
        );

        List<ExecOwnershipDetail> entities = detailList.stream()
                                                       .map(ExecOwnershipDetail::create)
                                                       .collect(Collectors.toCollection(() -> new ArrayList<>(detailList.size())));
        execOwnershipDetailRepo.insertNativeBatch(entities, 500);
    }


    private void saveLargeHoldingsDetailData(LargeHoldings entity) {
        List<LargeHoldingsDetailCrawlingDTO> detailList = dart.getLargeHoldingsDetailCrawling(entity.getRceptNo(), entity.getCorpCode(), entity.getCorpName());

        List<LargeHoldingsDetail> entities = detailList.stream()
                                                       .map(largeHoldingsDetailCrawlingDTO -> LargeHoldingsDetail.create(
                                                               largeHoldingsDetailCrawlingDTO.getRceptNo(),
                                                               largeHoldingsDetailCrawlingDTO.getCorpCode(),
                                                               largeHoldingsDetailCrawlingDTO.getCorpName(),
                                                               largeHoldingsDetailCrawlingDTO.getLargeHoldingsName(),
                                                               largeHoldingsDetailCrawlingDTO.getBirthDateOrBizRegNum(),
                                                               largeHoldingsDetailCrawlingDTO.getTradeDt(),
                                                               largeHoldingsDetailCrawlingDTO.getTradeReason(),
                                                               largeHoldingsDetailCrawlingDTO.getStockType(),
                                                               largeHoldingsDetailCrawlingDTO.getBeforeStockAmount(),
                                                               largeHoldingsDetailCrawlingDTO.getChangeStockAmount(),
                                                               largeHoldingsDetailCrawlingDTO.getAfterStockAmount(),
                                                               largeHoldingsDetailCrawlingDTO.getUnitStockPrice(),
                                                               largeHoldingsDetailCrawlingDTO.getCurrencyType(),
                                                               largeHoldingsDetailCrawlingDTO.getTotalStockPrice()
                                                       ))
                                                       .collect(Collectors.toCollection(() -> new ArrayList<>(detailList.size())));
        largeHoldingsDetailRepo.insertNativeBatch(entities, 500);
    }

    private void saveLargeHoldingsStkrtData(LargeHoldings entity) {
        List<LargeHoldingsStkrtCrawlingDTO> stkrtList = dart.getLargeHoldingsStkrtCrawling(entity.getRceptNo(), entity.getCorpCode(), entity.getCorpName());

        List<LargeHoldingsStkrt> entities = stkrtList.stream()
                                                     .map(largeHoldingsStkrtCrawlingDTO -> LargeHoldingsStkrt.create(
                                                                     largeHoldingsStkrtCrawlingDTO.getRceptNo(),
                                                                     largeHoldingsStkrtCrawlingDTO.getCorpCode(),
                                                                     largeHoldingsStkrtCrawlingDTO.getCorpName(),
                                                                     largeHoldingsStkrtCrawlingDTO.getLargeHoldingsName(),
                                                                     largeHoldingsStkrtCrawlingDTO.getBirthDateOrBizRegNum(),
                                                                     largeHoldingsStkrtCrawlingDTO.getTotalStockAmount(),
                                                                     largeHoldingsStkrtCrawlingDTO.getStkrt()
                                                             )
                                                     )
                                                     .collect(Collectors.toCollection(() -> new ArrayList<>(stkrtList.size())));

        largeHoldingsStkrtRepo.insertNativeBatch(entities, 500);
    }
}
