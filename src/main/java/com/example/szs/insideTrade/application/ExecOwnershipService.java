package com.example.szs.insideTrade.application;

import com.example.szs.insideTrade.application.dto.ExecOwnershipDetailDTO;
import com.example.szs.insideTrade.domain.CorpInfo;
import com.example.szs.insideTrade.domain.CorpInfoRepo;
import com.example.szs.insideTrade.domain.ExecOwnership;
import com.example.szs.insideTrade.domain.ExecOwnershipDetail;
import com.example.szs.insideTrade.domain.ExecOwnershipDetailRepo;
import com.example.szs.insideTrade.domain.ExecOwnershipDomainService;
import com.example.szs.insideTrade.domain.ExecOwnershipRepo;
import com.example.szs.insideTrade.presentation.dto.request.ExecOwnershipDetailSearchConditionReqDTO;
import com.example.szs.insideTrade.presentation.dto.response.ExecOwnershipResDTO;
import com.example.szs.model.dto.MessageDto;
import com.example.szs.insideTrade.presentation.dto.response.PageResDTO;
import com.example.szs.model.eNum.redis.ChannelType;
import com.example.szs.model.eNum.stock.SellOrBuyType;
import com.example.szs.module.ApiResponse;
import com.example.szs.service.stock.PushService;
import com.example.szs.utils.jpa.EntityToDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class ExecOwnershipService {
    private final CorpInfoRepo corpInfoRepo;
    private final ExecOwnershipDomainService execOwnershipDomainService;
    private final ScrapingService scrapingService;
    private final ExecOwnershipDetailRepo execOwnershipDetailRepo;
    private final ExecOwnershipRepo execOwnershipRepo;

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

    public PageResDTO getSearchPageExecOwnershipDetail(ExecOwnershipDetailSearchConditionReqDTO condition, Pageable pageable) {
        Page<ExecOwnershipDetail> page = execOwnershipDetailRepo.searchPage(condition, pageable);
        return PageResDTO.builder()
                         .content(page.getContent())
                         .totalElements(page.getTotalElements())
                         .totalPages(page.getTotalPages())
                         .build();
    }

    public List<ExecOwnershipResDTO> getStockCntTop5(String corpCode) {
        List<ExecOwnership>       execOwnershipList = execOwnershipRepo.getExecOwnershipOrderSpStockLmpCnt(corpCode);
        List<ExecOwnershipResDTO> response          = new ArrayList<>(execOwnershipList.size());

        for (ExecOwnership execOwnership : execOwnershipList) {
            EntityToDtoMapper.mapEntityToDto(execOwnership, ExecOwnershipResDTO.class).ifPresent(response::add);
        }

        return response.stream()
                       .limit(5)
                       .toList();
    }

    public List<ExecOwnershipResDTO> getRatio(String corpCode) {
        List<ExecOwnership>       execOwnershipList = execOwnershipRepo.getExecOwnershipOrderSpStockLmpCnt(corpCode);
        List<ExecOwnershipResDTO> response          = new ArrayList<>(execOwnershipList.size());

        for (ExecOwnership execOwnership : execOwnershipList) {
            EntityToDtoMapper.mapEntityToDto(execOwnership, ExecOwnershipResDTO.class).ifPresent(response::add);
        }

        return response;
    }

    public List<ExecOwnershipDetail> getExecOwnershipTradeList(ExecOwnershipDetailSearchConditionReqDTO condition) {
        return execOwnershipDetailRepo.getExecOwnershipDetailList(condition);
    }

    public List<ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse> getTop5StockTrade(String tradeDtGoe, String tradeDtLoe) {
        ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse buy = ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
                                                                                                                 .sellOrBuyType(SellOrBuyType.BUY.getCode())
                                                                                                                 .top5StockDetailDTOList(execOwnershipDetailRepo.getTopStockDetail(ExecOwnershipDetailSearchConditionReqDTO.builder()
                                                                                                                                                                                                                           .tradeDtGoe(tradeDtGoe)
                                                                                                                                                                                                                           .tradeDtLoe(tradeDtLoe)
                                                                                                                                                                                                                           .changeStockAmountGt(0L)
                                                                                                                                                                                                                           .limit(5L)
                                                                                                                                                                                                                           .build()))
                                                                                                                 .build();

        ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse sell = ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
                                                                                                                  .sellOrBuyType(SellOrBuyType.SELL.getCode())
                                                                                                                  .top5StockDetailDTOList(execOwnershipDetailRepo.getTopStockDetail(ExecOwnershipDetailSearchConditionReqDTO.builder()
                                                                                                                                                                                                                            .tradeDtGoe(tradeDtGoe)
                                                                                                                                                                                                                            .tradeDtLoe(tradeDtLoe)
                                                                                                                                                                                                                            .changeStockAmountLt(0L)
                                                                                                                                                                                                                            .limit(5L)
                                                                                                                                                                                                                            .build()))
                                                                                                                  .build();

        return Arrays.asList(sell, buy);
    }

    public List<ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse>  getTopStockTradeTotal(String tradeDtGoe, String tradeDtLoe, SellOrBuyType sellOrBuyType) {
        ExecOwnershipDetailSearchConditionReqDTO requestBuy = ExecOwnershipDetailSearchConditionReqDTO.builder()
                                                                                                      .tradeDtGoe(tradeDtGoe)
                                                                                                      .tradeDtLoe(tradeDtLoe)
                                                                                                      .changeStockAmountGt(0L)
                                                                                                      .build();
        ExecOwnershipDetailSearchConditionReqDTO requestSell = ExecOwnershipDetailSearchConditionReqDTO.builder()
                                                                                                       .tradeDtGoe(tradeDtGoe)
                                                                                                       .tradeDtLoe(tradeDtLoe)
                                                                                                       .changeStockAmountLt(0L)
                                                                                                       .build();
        return switch (sellOrBuyType) {
            case BUY ->
                    List.of(ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
                                                                             .sellOrBuyType(SellOrBuyType.BUY.getCode())
                                                                             .top5StockDetailDTOList(execOwnershipDetailRepo.getTopStockDetail(requestBuy))
                                                                             .build());
            case SELL ->
                    List.of(ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
                                                                             .sellOrBuyType(SellOrBuyType.SELL.getCode())
                                                                             .top5StockDetailDTOList(execOwnershipDetailRepo.getTopStockDetail(requestSell))
                                                                             .build());
            case ALL ->
                    Arrays.asList(
                            ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
                                                                             .sellOrBuyType(SellOrBuyType.BUY.getCode())
                                                                             .top5StockDetailDTOList(execOwnershipDetailRepo.getTopStockDetail(requestBuy))
                                                                             .build(),
                            ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
                                                                             .sellOrBuyType(SellOrBuyType.SELL.getCode())
                                                                             .top5StockDetailDTOList(execOwnershipDetailRepo.getTopStockDetail(requestSell))
                                                                             .build()
            );
        };
    }

    public List<ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse> getMonthlyTradeCnt(String corpCode) {
        assert (corpCode != null) : "corpCode not null";
        // 매월 매도건수
        ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse sell = ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse.builder()
                                                                                                                        .sellOrBuyType(SellOrBuyType.SELL.getCode())
                                                                                                                        .monthlyCountDTOList(execOwnershipDetailRepo.getMonthlyTradeCnt(corpCode, true))
                                                                                                                        .build();

        // 매월 매수건수
        ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse buy = ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse.builder()
                                                                                                                       .sellOrBuyType(SellOrBuyType.BUY.getCode())
                                                                                                                       .monthlyCountDTOList(execOwnershipDetailRepo.getMonthlyTradeCnt(corpCode, false))
                                                                                                                       .build();
        return Arrays.asList(sell, buy);
    }
}
