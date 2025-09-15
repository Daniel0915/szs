package com.example.szs.insideTrade.application;

import com.example.szs.insideTrade.domain.LargeHoldings;
import com.example.szs.insideTrade.domain.LargeHoldingsDetail;
import com.example.szs.insideTrade.domain.LargeHoldingsDetailRepo;
import com.example.szs.insideTrade.domain.LargeHoldingsStkrt;
import com.example.szs.insideTrade.domain.LargeHoldingsStkrtRepo;
import com.example.szs.insideTrade.infrastructure.client.Dart;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsDetailCrawlingDTO;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsStkrtCrawlingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapingService {
    private final Dart dart;
    private final LargeHoldingsDetailRepo largeHoldingsDetailRepo;
    private final LargeHoldingsStkrtRepo largeHoldingsStkrtRepo;

    @Transactional
    public void updateLargeHoldingsScrapingData(List<LargeHoldings> insertList) {
        for (LargeHoldings entity : insertList) {
            saveLargeHoldingsDetailData(entity);
            saveLargeHoldingsStkrtData(entity);
        }
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
                                                       .toList();
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
                                                     .toList();

        largeHoldingsStkrtRepo.insertNativeBatch(entities, 500);
    }
}
