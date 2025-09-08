package com.example.szs.insideTrade.application;

import com.example.szs.insideTrade.domain.CorpInfo;
import com.example.szs.insideTrade.domain.CorpInfoRepo;
import com.example.szs.insideTrade.domain.LargeHoldings;
import com.example.szs.insideTrade.domain.LargeHoldingsDomainService;
import com.example.szs.insideTrade.infrastructure.push.SsePush;
import com.example.szs.insideTrade.infrastructure.push.dto.MessageDTO;
import com.example.szs.model.eNum.redis.ChannelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@EnableScheduling
public class LargeHoldingsService {
    private final LargeHoldingsDomainService largeHoldingsDomainService;
    private final ScrapingService scrapingService;
    private final SsePush ssePush;
    private final CorpInfoRepo corpInfoJpaRepo;

    public LargeHoldingsService(LargeHoldingsDomainService largeHoldingsDomainService,
                                ScrapingService scrapingService,
                                SsePush ssePush,
                                @Qualifier("corpInfoJpaRepo") CorpInfoRepo corpInfoJpaRepo
    ) {
        this.largeHoldingsDomainService = largeHoldingsDomainService;
        this.scrapingService = scrapingService;
        this.ssePush = ssePush;
        this.corpInfoJpaRepo = corpInfoJpaRepo;
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void insertData() {
        List<CorpInfo> findCorpInfoList = corpInfoJpaRepo.findAll();

        // TODO : test code
        CorpInfo corpInfo = CorpInfo.builder()
                .corpCode("00126380")
                .corpName("삼성전자")
                .regDt("20250226224406")
                                .build();
        // 1. 전체 회사를 조회 [내부 DB] + 회사별 외부 다트 호출해서, 지분공시 변경 데이터 조회 [외부 호출] + 내부 DB 와 외부 다트 비교, 새로운 데이터를 내부 DB 저장(외부 호출에 대한 데이터 저장) [내부 DB]
        List<LargeHoldings> insertList = largeHoldingsDomainService.saveRecentLargeHoldings(corpInfo);
        // 2. 1번 데이터는 정확하지 않기 떄문에, 지분 공시의 고유 넘버를 가지고, 외부 다트 공시 웹 크롤링 [외부 호출] + 크롤링 데이터를 내부 DB 저장 [내부 DB]
        scrapingService.updateLargeHoldingsScrapingData(insertList);

        // 3. 저장된 데이터는 고객들에게 PUSH 전송 [내부 푸시 전송]
        // push message send
        ssePush.sendMessage(MessageDTO.builder()
                                      .message(corpInfo.getCorpName())
                                      .corpCode(corpInfo.getCorpCode())
                                      .channelType(ChannelType.STOCK_CHANGE_NOTIFY_LARGE_HOLDINGS)
                                      .build());

//        for (CorpInfo corpInfo : findCorpInfoList) {
//            // TODO : 예외 발생 처리
//            // TODO : 예외 발생 시, 로그 처리
//            // TODO : 배치 단위 저장
//            // 1. 전체 회사를 조회 [내부 DB] + 회사별 외부 다트 호출해서, 지분공시 변경 데이터 조회 [외부 호출] + 내부 DB 와 외부 다트 비교, 새로운 데이터를 내부 DB 저장(외부 호출에 대한 데이터 저장) [내부 DB]
//            List<LargeHoldings> insertList = largeHoldingsDomainService.saveRecentLargeHoldings(corpInfo);
//            // 2. 1번 데이터는 정확하지 않기 떄문에, 지분 공시의 고유 넘버를 가지고, 외부 다트 공시 웹 크롤링 [외부 호출] + 크롤링 데이터를 내부 DB 저장 [내부 DB]
//            scrapingService.updateLargeHoldingsScrapingData(insertList);
//
//            // 3. 저장된 데이터는 고객들에게 PUSH 전송 [내부 푸시 전송]
//            // push message send
//            ssePush.sendMessage(MessageDTO.builder()
//                                          .message(corpInfo.getCorpName())
//                                          .corpCode(corpInfo.getCorpCode())
//                                          .channelType(ChannelType.STOCK_CHANGE_NOTIFY_LARGE_HOLDINGS)
//                                          .build());
//
//
//        }
    }
}
