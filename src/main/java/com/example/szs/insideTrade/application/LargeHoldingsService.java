package com.example.szs.insideTrade.application;

import com.example.szs.insideTrade.domain.CorpInfo;
import com.example.szs.insideTrade.domain.CorpInfoRepo;
import com.example.szs.insideTrade.domain.LargeHoldings;
import com.example.szs.insideTrade.domain.LargeHoldingsRepo;
import com.example.szs.insideTrade.infrastructure.client.Dart;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsInsiderTradeApiRes;
import com.example.szs.insideTrade.infrastructure.db.queryDSL.LargeHoldingsSearchCondition;
import com.example.szs.insideTrade.infrastructure.push.SsePush;
import com.example.szs.insideTrade.infrastructure.push.dto.MessageDTO;
import com.example.szs.model.eNum.redis.ChannelType;
import com.example.szs.utils.money.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@Slf4j
@EnableScheduling
public class LargeHoldingsService {
    private final ScrapingService scrapingService;
    private final CorpInfoRepo corpInfoJpaRepo;
    private final LargeHoldingsRepo largeHoldingsRepo;

    private final Dart dart;
    private final SsePush ssePush;

    public LargeHoldingsService(ScrapingService scrapingService,
                                @Qualifier("corpInfoJpaRepo") CorpInfoRepo corpInfoJpaRepo,
                                @Qualifier("largeHoldingsQueryDSLRepo") LargeHoldingsRepo largeHoldingsRepo,
                                Dart dart,
                                SsePush ssePush) {
        this.scrapingService = scrapingService;
        this.corpInfoJpaRepo = corpInfoJpaRepo;
        this.largeHoldingsRepo = largeHoldingsRepo;
        this.dart = dart;
        this.ssePush = ssePush;
    }

    @Transactional
    @Scheduled(cron = "0 0 9 * * ?")
    public void insertData() {
        // TODO : 내부 + 외부 호출 구분해서 transactional 설정(근데 단순히 읽기라서 안해도 될듯도 하다)
        // 회사 이름 및 코드 조회
        List<CorpInfo> findCorpInfoList = corpInfoJpaRepo.findAll();

        for (CorpInfo corpInfo : findCorpInfoList) {
            // 회사별 지분공시 외부 Dart 호출로 조회
            Optional<LargeHoldingsInsiderTradeApiRes> resOptional = dart.findLargeHoldingsInsiderTrade(corpInfo);
            if (resOptional.isEmpty() || CollectionUtils.isEmpty(resOptional.get().getList())) {
                continue;
            }
            List<LargeHoldingsInsiderTradeApiRes.LargeHolding> resList = resOptional.get().getList();

            // 가장 최근 내부 DB에 저장된 지분 공시 데이터 조회
            Optional<LargeHoldings> optionalLargeHoldings = largeHoldingsRepo.findLatestRecordBy(LargeHoldingsSearchCondition.builder()
                                                                                                                             .corpCode(corpInfo.getCorpCode())
                                                                                                                             .orderColumn(LargeHoldings.Fields.rceptNo)
                                                                                                                             .isDescending(true)
                                                                                                                             .build());
            // 외부 호출 과 내부 DB 데이터 비교 후, 내부 DB 에 없는 데이터 저장
            List<LargeHoldingsInsiderTradeApiRes.LargeHolding> largeHoldingList = new ArrayList<>();
            if (optionalLargeHoldings.isPresent()) {
                String lastRceptNo = optionalLargeHoldings.get().getRceptNo();
                int startIndex = IntStream.range(0, resList.size())
                                          .filter(index -> Objects.equals(resList.get(index).getRceptNo(), lastRceptNo))
                                          .findFirst()
                                          .orElse(-1);

                int skipCount = (startIndex == -1) ? 0 : startIndex + 1;
                largeHoldingList = resList.stream()
                                          .skip(skipCount)
                                          .collect(Collectors.toList());
            } else {
                largeHoldingList = resList;
            }

            List<LargeHoldings> insertList = largeHoldingList.stream()
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
                                                             .collect(Collectors.toList());
            largeHoldingsRepo.saveAll(insertList);

            // 웹 스크래핑
            scrapingService.updateScrapingData(insertList);

            // push message send
            ssePush.sendMessage(MessageDTO.builder()
                                          .message(corpInfo.getCorpName())
                                          .corpCode(corpInfo.getCorpCode())
                                          .channelType(ChannelType.STOCK_CHANGE_NOTIFY_LARGE_HOLDINGS)
                                          .build());
        }
    }
}
