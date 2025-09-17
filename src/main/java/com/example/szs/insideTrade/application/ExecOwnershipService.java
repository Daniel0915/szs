package com.example.szs.insideTrade.application;

import com.example.szs.insideTrade.domain.CorpInfo;
import com.example.szs.insideTrade.domain.CorpInfoRepo;
import com.example.szs.insideTrade.domain.ExecOwnership;
import com.example.szs.insideTrade.domain.ExecOwnershipDomainService;
import com.example.szs.insideTrade.domain.ExecOwnershipRepository;
import com.example.szs.insideTrade.infrastructure.client.Dart;
import com.example.szs.model.dto.MessageDto;
import com.example.szs.model.dto.corpInfo.CorpInfoDTO;
import com.example.szs.model.dto.execOwnership.EOResponseDTO;
import com.example.szs.model.dto.execOwnership.ExecOwnershipDTO;
import com.example.szs.model.dto.execOwnership.ExecOwnershipDetailDTO;
import com.example.szs.model.dto.page.PageDTO;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.model.eNum.redis.ChannelType;
import com.example.szs.model.eNum.stock.SellOrBuyType;
import com.example.szs.model.queryDSLSearch.ExecOwnershipDetailSearchCondition;
import com.example.szs.model.queryDSLSearch.ExecOwnershipSearchCondition;
import com.example.szs.module.ApiResponse;
import com.example.szs.module.stock.WebCrawling;
import com.example.szs.repository.stock.CorpInfoRepositoryCustom;
import com.example.szs.repository.stock.ExecOwnershipDetailRepositoryCustom;
import com.example.szs.repository.stock.ExecOwnershipRepositoryCustom;
import com.example.szs.service.stock.PushService;
import com.example.szs.utils.jpa.EntityToDtoMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class ExecOwnershipService {
    @Value("${dart.uri.base}")
    private String baseUri;
    @Value("${dart.uri.execOwnership}")
    private String path;
    @Value("${corp.code.key}")
    private String corpCodeKey;
    @Value("${dart.key}")
    private String dartKey;
    @Value("${dart.value}")
    private String dartValue;

    private final CorpInfoRepo corpInfoRepo;
    private final ExecOwnershipDomainService execOwnershipDomainService;
    private final ScrapingService scrapingService;

    private final PushService pushService;
    private final ApiResponse apiResponse;

    @Transactional
    @Scheduled(cron = "0 0 9 * * ?")
    public void insertData() throws Exception {
        List<CorpInfo> findCorpInfoList = corpInfoRepo.findAll();

        for (CorpInfo corpInfo : findCorpInfoList) {
            // 1. 전체 회사를 조회 [내부 DB] + 회사별 외부 다트 호출해서, 지분공시 변경 데이터 조회 [외부 호출] + 내부 DB 와 외부 다트 비교, 새로운 데이터를 내부 DB 저장(외부 호출에 대한 데이터 저장) [내부 DB]
            List<ExecOwnership> insertList = execOwnershipDomainService.saveRecentLExecOwnership(corpInfo);
            // 2. 1번 데이터는 정확하지 않기 떄문에, 지분 공시의 고유 넘버를 가지고, 외부 다트 공시 웹 크롤링 [외부 호출] + 크롤링 데이터를 내부 DB 저장 [내부 DB]
            scrapingService.updateExecOwnershipsScrapingData(insertList);

            // 3. 저장된 데이터는 고객들에게 PUSH 전송 [내부 푸시 전송]
            pushService.sendMessage(MessageDto.builder()
                                              .message(corpInfo.getCorpName())
                                              .corpCode(corpInfo.getCorpCode())
                                              .channelType(ChannelType.STOCK_CHANGE_EXECOWNERSHIP)
                                              .build());
        }
    }

    public ResponseEntity<?> getSearchPageExecOwnershipDetail(ExecOwnershipDetailSearchCondition condition, Pageable pageable) {
        Page<ExecOwnershipDetailDTO> page = execOwnershipDetailRepositoryCustom.searchPage(condition, pageable);

        PageDTO pageDTO = PageDTO.builder()
                                 .content(page.getContent())
                                 .totalElements(page.getTotalElements())
                                 .totalPages(page.getTotalPages())
                                 .build();
        return apiResponse.makeResponse(ResStatus.SUCCESS, pageDTO);
    }

    public ResponseEntity<?> getStockCntTop5(String corpCode) {
        return apiResponse.makeResponse(ResStatus.SUCCESS, execOwnershipRepositoryCustom.getExecOwnershipOrderSpStockLmpCnt(corpCode).stream().limit(5));
    }

    public ResponseEntity<?> getRatio(String corpCode) {
        return apiResponse.makeResponse(ResStatus.SUCCESS, execOwnershipRepositoryCustom.getExecOwnershipOrderSpStockLmpCnt(corpCode));
    }

    public ResponseEntity<?> getExecOwnershipTradeList(ExecOwnershipDetailSearchCondition condition) {
        return apiResponse.makeResponse(ResStatus.SUCCESS, execOwnershipDetailRepositoryCustom.getExecOwnershipDetailDTOList(condition));
    }

    public List<ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse> getTop5StockTrade(String tradeDtGoe, String tradeDtLoe) {
        ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse buy = ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
                                                                                                                 .sellOrBuyType(SellOrBuyType.BUY.getCode())
                                                                                                                 .top5StockDetailDTOList(execOwnershipDetailRepositoryCustom.getTopStockDetail(ExecOwnershipDetailSearchCondition.builder()
                                                                                                                                                                                                                                 .tradeDtGoe(tradeDtGoe)
                                                                                                                                                                                                                                 .tradeDtLoe(tradeDtLoe)
                                                                                                                                                                                                                                 .changeStockAmountGt(0L)
                                                                                                                                                                                                                                 .limit(5L)
                                                                                                                                                                                                                                 .build()))
                                                                                                                 .build();

        ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse sell = ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
                                                                                                                  .sellOrBuyType(SellOrBuyType.SELL.getCode())
                                                                                                                  .top5StockDetailDTOList(execOwnershipDetailRepositoryCustom.getTopStockDetail(ExecOwnershipDetailSearchCondition.builder()
                                                                                                                                                                                                                                  .tradeDtGoe(tradeDtGoe)
                                                                                                                                                                                                                                  .tradeDtLoe(tradeDtLoe)
                                                                                                                                                                                                                                  .changeStockAmountLt(0L)
                                                                                                                                                                                                                                  .limit(5L)
                                                                                                                                                                                                                                  .build()))
                                                                                                                  .build();

        return Arrays.asList(sell, buy);
    }

    public ResponseEntity<?> getTopStockTradeTotal(String tradeDtGoe, String tradeDtLoe, SellOrBuyType sellOrBuyType) {
        return switch (sellOrBuyType) {
            case BUY ->
                    apiResponse.makeResponse(ResStatus.SUCCESS, ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
                                                                                                                 .sellOrBuyType(SellOrBuyType.BUY.getCode())
                                                                                                                 .top5StockDetailDTOList(execOwnershipDetailRepositoryCustom.getTopStockDetail(ExecOwnershipDetailSearchCondition.builder()
                                                                                                                                                                                                                                 .tradeDtGoe(tradeDtGoe)
                                                                                                                                                                                                                                 .tradeDtLoe(tradeDtLoe)
                                                                                                                                                                                                                                 .changeStockAmountGt(0L)
                                                                                                                                                                                                                                 .build()))
                                                                                                                 .build());
            case SELL ->
                    apiResponse.makeResponse(ResStatus.SUCCESS, ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
                                                                                                                 .sellOrBuyType(SellOrBuyType.SELL.getCode())
                                                                                                                 .top5StockDetailDTOList(execOwnershipDetailRepositoryCustom.getTopStockDetail(ExecOwnershipDetailSearchCondition.builder()
                                                                                                                                                                                                                                 .tradeDtGoe(tradeDtGoe)
                                                                                                                                                                                                                                 .tradeDtLoe(tradeDtLoe)
                                                                                                                                                                                                                                 .changeStockAmountLt(0L)
                                                                                                                                                                                                                                 .build()))
                                                                                                                 .build());
            case ALL ->
                    apiResponse.makeResponse(ResStatus.SUCCESS, Arrays.asList(
                            ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
                                                                             .sellOrBuyType(SellOrBuyType.BUY.getCode())
                                                                             .top5StockDetailDTOList(execOwnershipDetailRepositoryCustom.getTopStockDetail(ExecOwnershipDetailSearchCondition.builder()
                                                                                                                                                                                             .tradeDtGoe(tradeDtGoe)
                                                                                                                                                                                             .tradeDtLoe(tradeDtLoe)
                                                                                                                                                                                             .changeStockAmountGt(0L)
                                                                                                                                                                                             .build()))
                                                                             .build(),
                            ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
                                                                             .sellOrBuyType(SellOrBuyType.SELL.getCode())
                                                                             .top5StockDetailDTOList(execOwnershipDetailRepositoryCustom.getTopStockDetail(ExecOwnershipDetailSearchCondition.builder()
                                                                                                                                                                                             .tradeDtGoe(tradeDtGoe)
                                                                                                                                                                                             .tradeDtLoe(tradeDtLoe)
                                                                                                                                                                                             .changeStockAmountLt(0L)
                                                                                                                                                                                             .build()))
                                                                             .build()
                    ));
        };
    }

    public ResponseEntity<?> getMonthlyTradeCnt(String corpCode) {
        assert (corpCode != null) : "corpCode not null";
        // 매월 매도건수
        ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse sell = ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse.builder()
                                                                                                                        .sellOrBuyType(SellOrBuyType.SELL.getCode())
                                                                                                                        .monthlyCountDTOList(execOwnershipDetailRepositoryCustom.getMonthlyTradeCnt(corpCode, true))
                                                                                                                        .build();

        // 매월 매수건수
        ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse buy = ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse.builder()
                                                                                                                       .sellOrBuyType(SellOrBuyType.BUY.getCode())
                                                                                                                       .monthlyCountDTOList(execOwnershipDetailRepositoryCustom.getMonthlyTradeCnt(corpCode, false))
                                                                                                                       .build();




        List<ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse> responses = Arrays.asList(sell, buy);
        return apiResponse.makeResponse(ResStatus.SUCCESS, responses);
    }
}
