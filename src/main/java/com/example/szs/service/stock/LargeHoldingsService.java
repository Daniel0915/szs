//package com.example.szs.service.stock;
//
//import com.example.szs.insideTrade.domain.LargeHoldingsDetailEntity;
//import com.example.szs.insideTrade.domain.LargeHoldingsEntity;
//import com.example.szs.model.dto.MessageDto;
//import com.example.szs.model.dto.corpInfo.CorpInfoDTO;
//import com.example.szs.model.dto.largeHoldings.LHResponseDTO;
//import com.example.szs.model.dto.largeHoldings.LargeHoldingsDTO;
//import com.example.szs.model.dto.largeHoldings.LargeHoldingsDetailDTO;
//import com.example.szs.model.dto.largeHoldings.LargeHoldingsStkrtDTO;
//import com.example.szs.model.dto.page.PageDTO;
//import com.example.szs.model.eNum.ResStatus;
//import com.example.szs.model.eNum.redis.ChannelType;
//import com.example.szs.model.eNum.stock.SellOrBuyType;
//import com.example.szs.model.queryDSLSearch.LargeHoldingStkrtSearchCondition;
//import com.example.szs.model.queryDSLSearch.LargeHoldingsDetailSearchCondition;
//import com.example.szs.insideTrade.infrastructure.db.queryDSL.LargeHoldingsSearchCondition;
//import com.example.szs.module.ApiResponse;
//import com.example.szs.module.stock.WebCrawling;
//import com.example.szs.repository.stock.CorpInfoRepositoryCustom;
//import com.example.szs.repository.stock.LargeHoldingsDetailRepositoryCustom;
//import com.example.szs.repository.stock.LargeHoldingsRepositoryCustom;
//import com.example.szs.repository.stock.LargeHoldingsStkrtRepositoryCustom;
//import com.example.szs.utils.jpa.EntityToDtoMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional(readOnly = true)
//@RequiredArgsConstructor
//@Slf4j
//@EnableScheduling
//public class LargeHoldingsService {
//    @Value("${dart.uri.base}")
//    private String baseUri;
//    @Value("${dart.uri.largeHoldings}")
//    private String path;
//    @Value("${corp.code.key}")
//    private String corpCodeKey;
//    @Value("${dart.key}")
//    private String dartKey;
//    @Value("${dart.value}")
//    private String dartValue;
//
//    private final LargeHoldingsRepositoryCustom largeHoldingsRepositoryCustom;
//    private final LargeHoldingsDetailRepositoryCustom largeHoldingsDetailRepositoryCustom;
//    private final LargeHoldingsStkrtRepositoryCustom largeHoldingsStkrtRepositoryCustom;
//    private final CorpInfoRepositoryCustom corpInfoRepositoryCustom;
//
//    private final PushService pushService;
//    private final WebCrawling webCrawling;
//    private final ApiResponse apiResponse;
//
//    @Transactional
//    @Scheduled(cron = "0 0 9 * * ?")
//    public void insertData() {
//        List<CorpInfoDTO> corpInfoDTOList = corpInfoRepositoryCustom.getAllCorpInfoDTOList();
//
//        for (CorpInfoDTO dto : corpInfoDTOList) {
//            WebClient webClient = WebClient.builder()
//                                           .baseUrl(baseUri)
//                                           .build();
//
//            Mono<LHResponseDTO> lhResponseDtoMono = webClient.get()
//                                                             .uri(uriBuilder -> uriBuilder.path(path)
//                                                                                          .queryParam(dartKey, dartValue)
//                                                                                          .queryParam(corpCodeKey, dto.getCorpCode())
//                                                                                          .build()).retrieve().bodyToMono(LHResponseDTO.class);
//
//            LHResponseDTO lhResponseDTO = lhResponseDtoMono.block();
//            if (lhResponseDTO == null) {
//                return;
//            }
//
//            List<LargeHoldingsEntity> largeHoldingsEntityList = lhResponseDTO.toEntity();
//
//            Optional<LargeHoldingsDTO> optionalLargeHoldingsDTO = largeHoldingsRepositoryCustom.findLatestRecordBy(LargeHoldingsSearchCondition.builder()
//                                                                                                                                               .corpCode(dto.getCorpCode())
//                                                                                                                                               .orderColumn(LargeHoldingsEntity.Fields.rceptNo)
//                                                                                                                                               .isDescending(true)
//                                                                                                                                               .build());
//            if (optionalLargeHoldingsDTO.isEmpty()) {
//                largeHoldingsRepositoryCustom.saveAll(largeHoldingsEntityList);
//                if (!CollectionUtils.isEmpty(largeHoldingsEntityList)) {
//                    this.updateScraping(largeHoldingsEntityList.stream()
//                                                               .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsDTO.class).stream())
//                                                               .collect(Collectors.toList()));
//                }
//
//                pushService.sendMessage(MessageDto.builder()
//                                                  .message(largeHoldingsEntityList.get(0).getCorpName())
//                                                  .corpCode(largeHoldingsEntityList.get(0).getCorpCode())
//                                                  .channelType(ChannelType.STOCK_CHANGE_NOTIFY_LARGE_HOLDINGS)
//                                                  .build());
//                return;
//            }
//
//            LargeHoldingsEntity findLatestRecord = EntityToDtoMapper.mapEntityToDto(optionalLargeHoldingsDTO.get(), LargeHoldingsEntity.class).get();
//
//            Comparator<LargeHoldingsEntity> comparator = (o1, o2) -> {
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
//            largeHoldingsEntityList.sort(comparator);
//
//            int findIndex = Collections.binarySearch(largeHoldingsEntityList, findLatestRecord, comparator);
//
//            List<LargeHoldingsEntity> insertEntity = largeHoldingsEntityList.subList(findIndex + 1, largeHoldingsEntityList.size());
//            largeHoldingsRepositoryCustom.saveAll(insertEntity);
//
//            if (!CollectionUtils.isEmpty(largeHoldingsEntityList)) {
//                this.updateScraping(largeHoldingsEntityList.stream()
//                                                           .map(entity -> EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsDTO.class))
//                                                           .flatMap(Optional::stream)
//                                                           .toList());
//            }
//
//            if (insertEntity.isEmpty()) {
//                return;
//            }
//
//            List<LargeHoldingsDTO> largeHoldingsDTOList = new ArrayList<>();
//            for (LargeHoldingsEntity entity : insertEntity) {
//                Optional<LargeHoldingsDTO> dtoOptional = EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsDTO.class);
//                dtoOptional.ifPresent(largeHoldingsDTOList::add);
//            }
//
//            pushService.sendMessage(MessageDto.builder()
//                                              .message(insertEntity.get(0).getCorpName())
//                                              .corpCode(insertEntity.get(0).getCorpCode())
//                                              .channelType(ChannelType.STOCK_CHANGE_NOTIFY_LARGE_HOLDINGS)
//                                              .build());
//
//        }
//    }
//
//    public ResponseEntity<?> getSearchPageLargeHoldingsDetail(LargeHoldingsDetailSearchCondition condition, Pageable pageable) {
//        Page<LargeHoldingsDetailDTO> page = largeHoldingsDetailRepositoryCustom.searchPage(condition, pageable);
//
//        PageDTO pageDTO = PageDTO.builder()
//                                 .content(page.getContent())
//                                 .totalElements(page.getTotalElements())
//                                 .totalPages(page.getTotalPages())
//                                 .build();
//
//        return apiResponse.makeResponse(ResStatus.SUCCESS, pageDTO);
//    }
//
//    @Transactional
//    public ResponseEntity<?> updateScraping(List<LargeHoldingsDTO> largeHoldingsDTOList) {
//        for (LargeHoldingsDTO dto : largeHoldingsDTOList) {
//            try {
//                List<LargeHoldingsDetailDTO> largeHoldingsDetailDTOList = webCrawling.getLargeHoldingsDetailCrawling(dto.getRceptNo(), dto.getCorpCode(), dto.getCorpName());
//                largeHoldingsDetailRepositoryCustom.saveAll(largeHoldingsDetailDTOList);
//
//                // TODO : LargeHoldingsDetail <-> LargeHoldingsStkrt 일대일 관계 매핑해야함.
//                List<LargeHoldingsStkrtDTO> largeHoldingsStkrtDTOList = webCrawling.getLargeHoldingsStkrtCrawling(dto.getRceptNo(), dto.getCorpCode(), dto.getCorpName());
//                largeHoldingsStkrtRepositoryCustom.saveAll(largeHoldingsStkrtDTOList);
//            } catch (Exception e) {
//                e.printStackTrace();
//                log.error(dto.getRceptNo(), dto.getCorpCode(), dto.getCorpName());
//            }
//        }
//        return apiResponse.makeResponse(ResStatus.SUCCESS);
//    }
//
//    public ResponseEntity<?> getLargeHoldingsStockRatio(LargeHoldingStkrtSearchCondition condition) {
//        List<LargeHoldingsStkrtDTO> findLargeHoldingsStockRatioList = largeHoldingsStkrtRepositoryCustom.getLargeHoldingsStockRatio(condition.toBuilder()
//                                                                                                                                             .limit(1L)
//                                                                                                                                             .build());
//
//        List<LargeHoldingsStkrtDTO> filteredStkrtExcNullOrInit = findLargeHoldingsStockRatioList.stream()
//                                                                                                .filter(dto -> dto.getStkrt() != null && dto.getStkrt() != 0.0F)
//                                                                                                .collect(Collectors.toList());
//        return apiResponse.makeResponse(ResStatus.SUCCESS, filteredStkrtExcNullOrInit);
//    }
//
//    public ResponseEntity<?> getLargeHoldingsMonthlyTradeCnt(String corpCode) {
//        assert (corpCode != null) : "corpCode not null";
//
//        // 매월 매도건수
//        LargeHoldingsDetailDTO.SellOrBuyMonthlyCountResponse sell = LargeHoldingsDetailDTO.SellOrBuyMonthlyCountResponse.builder()
//                                                                                                                        .sellOrBuyType(SellOrBuyType.SELL.getCode())
//                                                                                                                        .monthlyCountDTOList(largeHoldingsDetailRepositoryCustom.getLargeHoldingsMonthlyTradeCnt(corpCode, true))
//                                                                                                                        .build();
//
//        // 매월 매수건수
//        LargeHoldingsDetailDTO.SellOrBuyMonthlyCountResponse buy = LargeHoldingsDetailDTO.SellOrBuyMonthlyCountResponse.builder()
//                                                                                                                        .sellOrBuyType(SellOrBuyType.BUY.getCode())
//                                                                                                                        .monthlyCountDTOList(largeHoldingsDetailRepositoryCustom.getLargeHoldingsMonthlyTradeCnt(corpCode, false))
//                                                                                                                        .build();
//
//        List<LargeHoldingsDetailDTO.SellOrBuyMonthlyCountResponse> responses = Arrays.asList(sell, buy);
//        return apiResponse.makeResponse(ResStatus.SUCCESS, responses);
//    }
//
//    public ResponseEntity<?> getLargeHoldingsStockRatioTop5(String corpCode) {
//        List<LargeHoldingsStkrtDTO> findLargeHoldingsStockRatioList = largeHoldingsStkrtRepositoryCustom.getLargeHoldingsStockRatio(LargeHoldingStkrtSearchCondition.builder()
//                                                                                                                                                                    .corpCode(corpCode)
//                                                                                                                                                                    .limit(1L)
//                                                                                                                                                                    .build());
//
//        List<LargeHoldingsStkrtDTO> top5List = findLargeHoldingsStockRatioList.size() > 5 ? findLargeHoldingsStockRatioList.stream().limit(5).collect(Collectors.toList()) : findLargeHoldingsStockRatioList;
//
//
//        return apiResponse.makeResponse(ResStatus.SUCCESS, top5List);
//    }
//
//    public ResponseEntity<?> getLargeHoldingsTradeDtBy(String corpCode, String largeHoldingsName) {
//        List<LargeHoldingsDetailDTO> largeHoldingsDetailDTOList = largeHoldingsDetailRepositoryCustom.getLargeHoldingsDetailDTOListBy(LargeHoldingsDetailSearchCondition.builder()
//                                                                                                                                                                        .corpCodeEq(corpCode)
//                                                                                                                                                                        .largeHoldingsNameEq(largeHoldingsName)
//                                                                                                                                                                        .orderColumn(LargeHoldingsDetailEntity.Fields.tradeDt)
//                                                                                                                                                                        .isDescending(false)
//                                                                                                                                                                        .build());
//
//        return apiResponse.makeResponse(ResStatus.SUCCESS, largeHoldingsDetailDTOList);
//    }
//
//    public List<LargeHoldingsDetailDTO.SellOrBuyTop5StockResponse> getTop5StockTrade(String tradeDtGoe, String tradeDtLoe) {
//        LargeHoldingsDetailDTO.SellOrBuyTop5StockResponse buy = LargeHoldingsDetailDTO.SellOrBuyTop5StockResponse.builder()
//                                                                                                                 .sellOrBuyType(SellOrBuyType.BUY.getCode())
//                                                                                                                 .top5StockDetailDTOList(largeHoldingsDetailRepositoryCustom.getTopStockDetail(LargeHoldingsDetailSearchCondition.builder()
//                                                                                                                                                                                                                                 .tradeDtGoe(tradeDtGoe)
//                                                                                                                                                                                                                                 .tradeDtLoe(tradeDtLoe)
//                                                                                                                                                                                                                                 .changeStockAmountGt(0L)
//                                                                                                                                                                                                                                 .limit(5L)
//                                                                                                                                                                                                                                 .build()))
//                                                                                                                 .build();
//
//        LargeHoldingsDetailDTO.SellOrBuyTop5StockResponse sell = LargeHoldingsDetailDTO.SellOrBuyTop5StockResponse.builder()
//                                                                                                                 .sellOrBuyType(SellOrBuyType.SELL.getCode())
//                                                                                                                 .top5StockDetailDTOList(largeHoldingsDetailRepositoryCustom.getTopStockDetail(LargeHoldingsDetailSearchCondition.builder()
//                                                                                                                                                                                                                                 .tradeDtGoe(tradeDtGoe)
//                                                                                                                                                                                                                                 .tradeDtLoe(tradeDtLoe)
//                                                                                                                                                                                                                                 .changeStockAmountLt(0L)
//                                                                                                                                                                                                                                 .limit(5L)
//                                                                                                                                                                                                                                 .build()))
//                                                                                                                 .build();
//
//        return Arrays.asList(sell, buy);
//    }
//
//    public ResponseEntity<?> getTopStockTradeTotal(String tradeDtGoe, String tradeDtLoe, SellOrBuyType sellOrBuyType) {
//        return switch (sellOrBuyType) {
//            case BUY ->
//                    apiResponse.makeResponse(ResStatus.SUCCESS, LargeHoldingsDetailDTO.SellOrBuyTop5StockResponse.builder()
//                                                                                                                 .sellOrBuyType(SellOrBuyType.BUY.getCode())
//                                                                                                                 .top5StockDetailDTOList(largeHoldingsDetailRepositoryCustom.getTopStockDetail(LargeHoldingsDetailSearchCondition.builder()
//                                                                                                                                                                                                                                 .tradeDtGoe(tradeDtGoe)
//                                                                                                                                                                                                                                 .tradeDtLoe(tradeDtLoe)
//                                                                                                                                                                                                                                 .changeStockAmountGt(0L)
//                                                                                                                                                                                                                                 .build()))
//                                                                                                                 .build());
//            case SELL ->
//                    apiResponse.makeResponse(ResStatus.SUCCESS, LargeHoldingsDetailDTO.SellOrBuyTop5StockResponse.builder()
//                                                                                                                 .sellOrBuyType(SellOrBuyType.SELL.getCode())
//                                                                                                                 .top5StockDetailDTOList(largeHoldingsDetailRepositoryCustom.getTopStockDetail(LargeHoldingsDetailSearchCondition.builder()
//                                                                                                                                                                                                                                 .tradeDtGoe(tradeDtGoe)
//                                                                                                                                                                                                                                 .tradeDtLoe(tradeDtLoe)
//                                                                                                                                                                                                                                 .changeStockAmountLt(0L)
//                                                                                                                                                                                                                                 .build()))
//                                                                                                                 .build());
//            case ALL ->
//                    apiResponse.makeResponse(ResStatus.SUCCESS, Arrays.asList(
//                            LargeHoldingsDetailDTO.SellOrBuyTop5StockResponse.builder()
//                                                                             .sellOrBuyType(SellOrBuyType.BUY.getCode())
//                                                                             .top5StockDetailDTOList(largeHoldingsDetailRepositoryCustom.getTopStockDetail(LargeHoldingsDetailSearchCondition.builder()
//                                                                                                                                                                                             .tradeDtGoe(tradeDtGoe)
//                                                                                                                                                                                             .tradeDtLoe(tradeDtLoe)
//                                                                                                                                                                                             .changeStockAmountGt(0L)
//                                                                                                                                                                                             .build()))
//                                                                             .build(),
//                            LargeHoldingsDetailDTO.SellOrBuyTop5StockResponse.builder()
//                                                                             .sellOrBuyType(SellOrBuyType.SELL.getCode())
//                                                                             .top5StockDetailDTOList(largeHoldingsDetailRepositoryCustom.getTopStockDetail(LargeHoldingsDetailSearchCondition.builder()
//                                                                                                                                                                                             .tradeDtGoe(tradeDtGoe)
//                                                                                                                                                                                             .tradeDtLoe(tradeDtLoe)
//                                                                                                                                                                                             .changeStockAmountLt(0L)
//                                                                                                                                                                                             .build()))
//                                                                             .build()
//                    ));
//        };
//    }
//
//    // TODO : 테스트 코드(추후 삭제)
//    @Transactional
//    public void insertDataTest() throws Exception {
//        List<CorpInfoDTO> corpInfoDTOList = corpInfoRepositoryCustom.getAllCorpInfoDTOList();
//        corpInfoDTOList = corpInfoDTOList.stream()
//                                         .filter(a -> Objects.equals(a.getCorpCode(), "01204056"))
//                                         .toList();
//
//
//
//        for (CorpInfoDTO dto : corpInfoDTOList) {
//            WebClient webClient = WebClient.builder()
//                                           .baseUrl(baseUri)
//                                           .build();
//
//            Mono<LHResponseDTO> lhResponseDtoMono = webClient.get()
//                                                             .uri(uriBuilder -> uriBuilder.path(path)
//                                                                                          .queryParam(dartKey, dartValue)
//                                                                                          .queryParam(corpCodeKey, dto.getCorpCode())
//                                                                                          .build()).retrieve().bodyToMono(LHResponseDTO.class);
//
//            LHResponseDTO lhResponseDTO = lhResponseDtoMono.block();
//            if (lhResponseDTO == null) {
//                return;
//            }
//
//            List<LargeHoldingsEntity> largeHoldingsEntityList = lhResponseDTO.toEntity();
//
//            Optional<LargeHoldingsDTO> optionalLargeHoldingsDTO = largeHoldingsRepositoryCustom.findLatestRecordBy(LargeHoldingsSearchCondition.builder()
//                                                                                                                                               .corpCode(dto.getCorpCode())
//                                                                                                                                               .orderColumn(LargeHoldingsEntity.Fields.rceptNo)
//                                                                                                                                               .isDescending(true)
//                                                                                                                                               .build());
//            if (optionalLargeHoldingsDTO.isEmpty()) {
//                largeHoldingsRepositoryCustom.saveAll(largeHoldingsEntityList);
//                if (!CollectionUtils.isEmpty(largeHoldingsEntityList)) {
//                    this.updateScraping(largeHoldingsEntityList.stream()
//                                                               .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsDTO.class).stream())
//                                                               .collect(Collectors.toList()));
//                }
//
//                pushService.sendMessage(MessageDto.builder()
//                                                  .message(largeHoldingsEntityList.get(0).getCorpName())
//                                                  .corpCode(largeHoldingsEntityList.get(0).getCorpCode())
//                                                  .channelType(ChannelType.STOCK_CHANGE_NOTIFY_LARGE_HOLDINGS)
//                                                  .build());
//                return;
//            }
//
//            LargeHoldingsEntity findLatestRecord = EntityToDtoMapper.mapEntityToDto(optionalLargeHoldingsDTO.get(), LargeHoldingsEntity.class).get();
//
//            Comparator<LargeHoldingsEntity> comparator = (o1, o2) -> {
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
//            largeHoldingsEntityList.sort(comparator);
//
//            int findIndex = Collections.binarySearch(largeHoldingsEntityList, findLatestRecord, comparator);
//
//            List<LargeHoldingsEntity> insertEntity = largeHoldingsEntityList.subList(findIndex + 1, largeHoldingsEntityList.size());
//            largeHoldingsRepositoryCustom.saveAll(insertEntity);
//
//            if (!CollectionUtils.isEmpty(largeHoldingsEntityList)) {
//                this.updateScraping(largeHoldingsEntityList.stream()
//                                                           .map(entity -> EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsDTO.class))
//                                                           .flatMap(Optional::stream)
//                                                           .toList());
//            }
//
//            if (insertEntity.isEmpty()) {
//                return;
//            }
//
//            List<LargeHoldingsDTO> largeHoldingsDTOList = new ArrayList<>();
//            for (LargeHoldingsEntity entity : insertEntity) {
//                Optional<LargeHoldingsDTO> dtoOptional = EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsDTO.class);
//                dtoOptional.ifPresent(largeHoldingsDTOList::add);
//            }
//
//            pushService.sendMessage(MessageDto.builder()
//                                              .message(insertEntity.get(0).getCorpName())
//                                              .corpCode(insertEntity.get(0).getCorpCode())
//                                              .channelType(ChannelType.STOCK_CHANGE_NOTIFY_LARGE_HOLDINGS)
//                                              .build());
//
//        }
//    }
//}
