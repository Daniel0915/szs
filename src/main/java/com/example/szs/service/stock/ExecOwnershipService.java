//package com.example.szs.service.stock;
//
//import com.example.szs.insideTrade.domain.ExecOwnership;
//import com.example.szs.model.dto.MessageDto;
//import com.example.szs.model.dto.corpInfo.CorpInfoDTO;
//import com.example.szs.model.dto.execOwnership.EOResponseDTO;
//import com.example.szs.model.dto.execOwnership.ExecOwnershipDTO;
//import com.example.szs.model.dto.execOwnership.ExecOwnershipDetailDTO;
//import com.example.szs.model.dto.page.PageDTO;
//import com.example.szs.model.eNum.ResStatus;
//import com.example.szs.model.eNum.redis.ChannelType;
//import com.example.szs.model.eNum.stock.SellOrBuyType;
//import com.example.szs.model.queryDSLSearch.ExecOwnershipDetailSearchCondition;
//import com.example.szs.model.queryDSLSearch.ExecOwnershipSearchCondition;
//import com.example.szs.module.ApiResponse;
//import com.example.szs.module.stock.WebCrawling;
//import com.example.szs.repository.stock.CorpInfoRepositoryCustom;
//import com.example.szs.repository.stock.ExecOwnershipDetailRepositoryCustom;
//import com.example.szs.insideTrade.domain.ExecOwnershipRepository;
//import com.example.szs.repository.stock.ExecOwnershipRepositoryCustom;
//import com.example.szs.utils.jpa.EntityToDtoMapper;
//import io.netty.channel.ChannelOption;
//import io.netty.handler.timeout.ReadTimeoutHandler;
//import io.netty.handler.timeout.WriteTimeoutHandler;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//import reactor.netty.http.client.HttpClient;
//import reactor.netty.tcp.TcpClient;
//
//import java.util.*;
//
//@Service
//@Transactional(readOnly = true)
//@RequiredArgsConstructor
//@Slf4j
//@EnableScheduling
//public class ExecOwnershipService {
//    @Value("${dart.uri.base}")
//    private String baseUri;
//    @Value("${dart.uri.execOwnership}")
//    private String path;
//    @Value("${corp.code.key}")
//    private String corpCodeKey;
//    @Value("${dart.key}")
//    private String dartKey;
//    @Value("${dart.value}")
//    private String dartValue;
//
//    private final ExecOwnershipRepository execOwnershipRepository;
//    private final ExecOwnershipRepositoryCustom execOwnershipRepositoryCustom;
//    private final ExecOwnershipDetailRepositoryCustom execOwnershipDetailRepositoryCustom;
//    private final CorpInfoRepositoryCustom corpInfoRepositoryCustom;
//
//    private final PushService pushService;
//    private final ApiResponse apiResponse;
//    private final WebCrawling webCrawling;
//
//    @Transactional
//    @Scheduled(cron = "0 0 9 * * ?")
//    public void insertData() {
//        List<CorpInfoDTO> corpInfoDTOList = corpInfoRepositoryCustom.getAllCorpInfoDTOList();
//
//        for (CorpInfoDTO dto : corpInfoDTOList) {
//            TcpClient tcpClient = TcpClient.create()
//                                           .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000 * 6)  // 연결 타임아웃 (60초)
//                                           .doOnConnected(conn ->
//                                                   conn.addHandlerLast(new ReadTimeoutHandler(60 * 5))   // 읽기 타임아웃 (5분)
//                                                       .addHandlerLast(new WriteTimeoutHandler(3600))  // 쓰기 타임아웃 (30초)
//                                           );
//
//
//            WebClient webClient = WebClient.builder()
//                                           .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
//                                           .baseUrl(baseUri)
//                                           .build();
//
//            Mono<EOResponseDTO> eoResponseDTOMono = webClient.get()
//                                                             .uri(uriBuilder -> uriBuilder.path(path)
//                                                                                          .queryParam(dartKey, dartValue)
//                                                                                          .queryParam(corpCodeKey, dto.getCorpCode())
//                                                                                          .build()).retrieve().bodyToMono(EOResponseDTO.class);
//
//            EOResponseDTO eoResponseDTO = eoResponseDTOMono.block();
//            if (eoResponseDTO == null) {
//                return;
//            }
//
//            List<ExecOwnership> execOwnershipList = eoResponseDTO.toEntity();
//
//            Optional<ExecOwnershipDTO> optionalExecOwnershipDTO = execOwnershipRepositoryCustom.findLatestRecordBy(ExecOwnershipSearchCondition.builder()
//                                                                                                                                               .corpCode(dto.getCorpCode())
//                                                                                                                                               .orderColumn(ExecOwnership.Fields.rceptNo)
//                                                                                                                                               .isDescending(true)
//                                                                                                                                               .build());
//            if (optionalExecOwnershipDTO.isEmpty()) {
//                execOwnershipRepository.saveAll(execOwnershipList);
//                if (!CollectionUtils.isEmpty(execOwnershipList)) {
//                    List<ExecOwnershipDetailDTO> insertExecExecOwnershipDetailDTOList = new ArrayList<>();
//                    for (ExecOwnership entity : execOwnershipList) {
//                        insertExecExecOwnershipDetailDTOList.addAll(webCrawling.getExecOwnershipDetailCrawling(
//                                entity.getRceptNo(),
//                                entity.getCorpCode(),
//                                entity.getCorpName(),
//                                entity.getRepror(),
//                                entity.getIsuExctvRgistAt(),
//                                entity.getIsuExctvOfcps(),
//                                entity.getIsuMainShrholdr()
//                        ));
//                    }
//                    execOwnershipDetailRepositoryCustom.saveAll(insertExecExecOwnershipDetailDTOList);
//                }
//                pushService.sendMessage(MessageDto.builder()
//                                                  .message(execOwnershipList.get(0).getCorpName())
//                                                  .corpCode(execOwnershipList.get(0).getCorpCode())
//                                                  .channelType(ChannelType.STOCK_CHANGE_NOTIFY_LARGE_HOLDINGS)
//                                                  .build());
//                return;
//            }
//
//            ExecOwnership findLatestRecord = EntityToDtoMapper.mapEntityToDto(optionalExecOwnershipDTO.get(), ExecOwnership.class).get();
//
//            Comparator<ExecOwnership> comparator = (o1, o2) -> {
//                String rceptNo_o1 = o1.getRceptNo();
//                String rceptNo_o2 = o2.getRceptNo();
//
//                if (!StringUtils.hasText(rceptNo_o1)) {
//                    return -1;
//                }
//
//                if (!StringUtils.hasText(rceptNo_o2)) {
//                    return 1;
//                }
//
//                return rceptNo_o1.compareTo(rceptNo_o2);
//            };
//
//            execOwnershipList.sort(comparator);
//
//            int findIndex = Collections.binarySearch(execOwnershipList, findLatestRecord, comparator);
//
//            List<ExecOwnership> insertEntity = execOwnershipList.subList(findIndex + 1, execOwnershipList.size());
//
//            if (insertEntity.isEmpty()) {
//                return;
//            }
//
//            execOwnershipRepository.saveAll(insertEntity);
//
//            List<ExecOwnershipDetailDTO> updateExecExecOwnershipDetailDTOList = new ArrayList<>();
//            for (ExecOwnership entity : insertEntity) {
//                updateExecExecOwnershipDetailDTOList.addAll(webCrawling.getExecOwnershipDetailCrawling(entity.getRceptNo(),
//                        entity.getCorpCode(),
//                        entity.getCorpName(),
//                        entity.getRepror(),
//                        entity.getIsuExctvRgistAt(),
//                        entity.getIsuExctvOfcps(),
//                        entity.getIsuMainShrholdr()));
//            }
//            execOwnershipDetailRepositoryCustom.saveAll(updateExecExecOwnershipDetailDTOList);
//
//            List<ExecOwnershipDTO> execOwnershipDTOList = new ArrayList<>();
//            for (ExecOwnership entity : insertEntity) {
//                Optional<ExecOwnershipDTO> dtoOptional = EntityToDtoMapper.mapEntityToDto(entity, ExecOwnershipDTO.class);
//                dtoOptional.ifPresent(execOwnershipDTOList::add);
//            }
//
//            pushService.sendMessage(MessageDto.builder()
//                                              .message(insertEntity.get(0).getCorpName())
//                                              .corpCode(insertEntity.get(0).getCorpCode())
//                                              .channelType(ChannelType.STOCK_CHANGE_EXECOWNERSHIP)
//                                              .build());
//        }
//    }
//    public ResponseEntity<?> getSearchPageExecOwnershipDetail(ExecOwnershipDetailSearchCondition condition, Pageable pageable) {
//        Page<ExecOwnershipDetailDTO> page = execOwnershipDetailRepositoryCustom.searchPage(condition, pageable);
//
//        PageDTO pageDTO = PageDTO.builder()
//                                 .content(page.getContent())
//                                 .totalElements(page.getTotalElements())
//                                 .totalPages(page.getTotalPages())
//                                 .build();
//        return apiResponse.makeResponse(ResStatus.SUCCESS, pageDTO);
//    }
//
//    public ResponseEntity<?> getStockCntTop5(String corpCode) {
//        return apiResponse.makeResponse(ResStatus.SUCCESS, execOwnershipRepositoryCustom.getExecOwnershipOrderSpStockLmpCnt(corpCode).stream().limit(5));
//    }
//
//    public ResponseEntity<?> getRatio(String corpCode) {
//        return apiResponse.makeResponse(ResStatus.SUCCESS, execOwnershipRepositoryCustom.getExecOwnershipOrderSpStockLmpCnt(corpCode));
//    }
//
//    public ResponseEntity<?> getExecOwnershipTradeList(ExecOwnershipDetailSearchCondition condition) {
//        return apiResponse.makeResponse(ResStatus.SUCCESS, execOwnershipDetailRepositoryCustom.getExecOwnershipDetailDTOList(condition));
//    }
//
//    public List<ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse> getTop5StockTrade(String tradeDtGoe, String tradeDtLoe) {
//        ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse buy = ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
//                                                                                                                 .sellOrBuyType(SellOrBuyType.BUY.getCode())
//                                                                                                                 .top5StockDetailDTOList(execOwnershipDetailRepositoryCustom.getTopStockDetail(ExecOwnershipDetailSearchCondition.builder()
//                                                                                                                                                                                                                                 .tradeDtGoe(tradeDtGoe)
//                                                                                                                                                                                                                                 .tradeDtLoe(tradeDtLoe)
//                                                                                                                                                                                                                                 .changeStockAmountGt(0L)
//                                                                                                                                                                                                                                 .limit(5L)
//                                                                                                                                                                                                                                 .build()))
//                                                                                                                 .build();
//
//        ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse sell = ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
//                                                                                                                  .sellOrBuyType(SellOrBuyType.SELL.getCode())
//                                                                                                                  .top5StockDetailDTOList(execOwnershipDetailRepositoryCustom.getTopStockDetail(ExecOwnershipDetailSearchCondition.builder()
//                                                                                                                                                                                                                                  .tradeDtGoe(tradeDtGoe)
//                                                                                                                                                                                                                                  .tradeDtLoe(tradeDtLoe)
//                                                                                                                                                                                                                                  .changeStockAmountLt(0L)
//                                                                                                                                                                                                                                  .limit(5L)
//                                                                                                                                                                                                                                  .build()))
//                                                                                                                  .build();
//
//        return Arrays.asList(sell, buy);
//    }
//
//    public ResponseEntity<?> getTopStockTradeTotal(String tradeDtGoe, String tradeDtLoe, SellOrBuyType sellOrBuyType) {
//        return switch (sellOrBuyType) {
//            case BUY ->
//                    apiResponse.makeResponse(ResStatus.SUCCESS, ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
//                                                                                                                 .sellOrBuyType(SellOrBuyType.BUY.getCode())
//                                                                                                                 .top5StockDetailDTOList(execOwnershipDetailRepositoryCustom.getTopStockDetail(ExecOwnershipDetailSearchCondition.builder()
//                                                                                                                                                                                                                                 .tradeDtGoe(tradeDtGoe)
//                                                                                                                                                                                                                                 .tradeDtLoe(tradeDtLoe)
//                                                                                                                                                                                                                                 .changeStockAmountGt(0L)
//                                                                                                                                                                                                                                 .build()))
//                                                                                                                 .build());
//            case SELL ->
//                    apiResponse.makeResponse(ResStatus.SUCCESS, ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
//                                                                                                                 .sellOrBuyType(SellOrBuyType.SELL.getCode())
//                                                                                                                 .top5StockDetailDTOList(execOwnershipDetailRepositoryCustom.getTopStockDetail(ExecOwnershipDetailSearchCondition.builder()
//                                                                                                                                                                                                                                 .tradeDtGoe(tradeDtGoe)
//                                                                                                                                                                                                                                 .tradeDtLoe(tradeDtLoe)
//                                                                                                                                                                                                                                 .changeStockAmountLt(0L)
//                                                                                                                                                                                                                                 .build()))
//                                                                                                                 .build());
//            case ALL ->
//                    apiResponse.makeResponse(ResStatus.SUCCESS, Arrays.asList(
//                            ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
//                                                                             .sellOrBuyType(SellOrBuyType.BUY.getCode())
//                                                                             .top5StockDetailDTOList(execOwnershipDetailRepositoryCustom.getTopStockDetail(ExecOwnershipDetailSearchCondition.builder()
//                                                                                                                                                                                             .tradeDtGoe(tradeDtGoe)
//                                                                                                                                                                                             .tradeDtLoe(tradeDtLoe)
//                                                                                                                                                                                             .changeStockAmountGt(0L)
//                                                                                                                                                                                             .build()))
//                                                                             .build(),
//                            ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse.builder()
//                                                                             .sellOrBuyType(SellOrBuyType.SELL.getCode())
//                                                                             .top5StockDetailDTOList(execOwnershipDetailRepositoryCustom.getTopStockDetail(ExecOwnershipDetailSearchCondition.builder()
//                                                                                                                                                                                             .tradeDtGoe(tradeDtGoe)
//                                                                                                                                                                                             .tradeDtLoe(tradeDtLoe)
//                                                                                                                                                                                             .changeStockAmountLt(0L)
//                                                                                                                                                                                             .build()))
//                                                                             .build()
//                    ));
//        };
//    }
//
//    public ResponseEntity<?> getMonthlyTradeCnt(String corpCode) {
//        assert (corpCode != null) : "corpCode not null";
//        // 매월 매도건수
//        ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse sell = ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse.builder()
//                                                                                                                        .sellOrBuyType(SellOrBuyType.SELL.getCode())
//                                                                                                                        .monthlyCountDTOList(execOwnershipDetailRepositoryCustom.getMonthlyTradeCnt(corpCode, true))
//                                                                                                                        .build();
//
//        // 매월 매수건수
//        ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse buy = ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse.builder()
//                                                                                                                       .sellOrBuyType(SellOrBuyType.BUY.getCode())
//                                                                                                                       .monthlyCountDTOList(execOwnershipDetailRepositoryCustom.getMonthlyTradeCnt(corpCode, false))
//                                                                                                                       .build();
//
//
//
//
//        List<ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse> responses = Arrays.asList(sell, buy);
//        return apiResponse.makeResponse(ResStatus.SUCCESS, responses);
//    }
//}
