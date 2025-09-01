package com.example.szs.insideTrade.application;

import com.example.szs.insideTrade.domain.CorpInfo;
import com.example.szs.insideTrade.domain.CorpInfoRepo;
import com.example.szs.insideTrade.domain.LargeHoldings;
import com.example.szs.insideTrade.domain.LargeHoldingsRepo;
import com.example.szs.insideTrade.infrastructure.client.Dart;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsDetailCrawling;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsInsiderTradeApiRes;
import com.example.szs.insideTrade.infrastructure.db.queryDSL.LargeHoldingsSearchCondition;
import com.example.szs.utils.money.NumberUtils;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class LargeHoldingsService {
    @Resource(name = "corpInfoJpaRepo")
    private final CorpInfoRepo corpInfoJpaRepo;
    @Resource(name = "largeHoldingsQueryDSLRepo")
    private final LargeHoldingsRepo largeHoldingsRepo;
    private final Dart dart;

    @Transactional
    @Scheduled(cron = "0 0 9 * * ?")
    public void insertData() {
        // TODO : 여기서부터 작업
        List<CorpInfo> findCorpInfoList = corpInfoJpaRepo.findAll();


        for (CorpInfo corpInfo : findCorpInfoList) {
            // TODO : 내부 + 외부 호출 구분해서 transactional 설정(근데 단순히 읽기라서 안해도 될듯도 하다)
            Optional<LargeHoldingsInsiderTradeApiRes> resOptional = dart.findLargeHoldingsInsiderTrade(corpInfo);
            if (resOptional.isEmpty() || CollectionUtils.isEmpty(resOptional.get().getList())) {
                continue;
            }

            Optional<LargeHoldings> optionalLargeHoldings = largeHoldingsRepo.findLatestRecordBy(LargeHoldingsSearchCondition.builder()
                                                                                                                             .corpCode(corpInfo.getCorpCode())
                                                                                                                             .orderColumn(LargeHoldings.Fields.rceptNo)
                                                                                                                             .isDescending(true)
                                                                                                                             .build());

            if (optionalLargeHoldings.isEmpty()) {
                List<LargeHoldings> insertList = resOptional.map(res -> res.getList()
                                                                           .stream()
                                                                           .map(largeHolding -> LargeHoldings.create(
                                                                                   largeHolding.getCorpCode(),
                                                                                   largeHolding.getCorpName(),
                                                                                   largeHolding.getRepror(),
                                                                                   NumberUtils.stringToLongConverter(largeHolding.getStkqy()),
                                                                                   NumberUtils.stringToLongConverter(largeHolding.getStkqyIrds()),
                                                                                   NumberUtils.stringToFloatConverter(largeHolding.getStkrt()),
                                                                                   NumberUtils.stringToFloatConverter(largeHolding.getStkrtIrds()),
                                                                                   largeHolding.getReportResn(),
                                                                                   largeHolding.getRceptDt()
                                                                           ))
                                                                           .toList()
                                                            )
                                                            .orElse(Collections.emptyList());
                largeHoldingsRepo.saveAll(insertList);
                // TODO : 외부 호출과 내부 DB transaction 나누기
                for(LargeHoldings scrapEntity : insertList) {
                    List<LargeHoldingsDetailCrawling> largeHoldingsDetailCrawlingList = dart.getLargeHoldingsDetailCrawling(scrapEntity.getRceptNo(), scrapEntity.getCorpCode(), scrapEntity.getCorpName());






                }












            }








        }


        // TODO : dart 외부 호출로 infra 옮기기


    }

}
