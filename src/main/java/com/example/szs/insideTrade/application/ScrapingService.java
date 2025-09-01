package com.example.szs.insideTrade.application;

import com.example.szs.insideTrade.domain.LargeHoldings;
import com.example.szs.insideTrade.domain.LargeHoldingsDetail;
import com.example.szs.insideTrade.domain.LargeHoldingsDetailRepo;
import com.example.szs.insideTrade.domain.LargeHoldingsStkrt;
import com.example.szs.insideTrade.domain.LargeHoldingsStkrtRepo;
import com.example.szs.insideTrade.infrastructure.client.Dart;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsDetailCrawlingDTO;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsStkrtCrawlingDTO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ScrapingService {
    private final Dart dart;
    private final LargeHoldingsDetailRepo largeHoldingsDetailRepo;
    private final LargeHoldingsStkrtRepo largeHoldingsStkrtRepo;

    public ScrapingService(Dart dart,
                           @Qualifier("largeHoldingsDetailQueryDSLRepo") LargeHoldingsDetailRepo largeHoldingsDetailRepo,
                           @Qualifier("largeHoldingsStkrtQueryDSLRepo") LargeHoldingsStkrtRepo largeHoldingsStkrtRepo) {
        this.dart = dart;
        this.largeHoldingsDetailRepo = largeHoldingsDetailRepo;
        this.largeHoldingsStkrtRepo = largeHoldingsStkrtRepo;
    }

    public void updateScrapingData(List<LargeHoldings> insertList) {
        for (LargeHoldings entity : insertList) {
            saveDetailData(entity);
            saveStkrtData(entity);
        }
    }

    private void saveDetailData(LargeHoldings entity) {
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

        largeHoldingsDetailRepo.saveAll(entities);
    }

    private void saveStkrtData(LargeHoldings entity) {
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

        largeHoldingsStkrtRepo.saveAll(entities);
    }
}
