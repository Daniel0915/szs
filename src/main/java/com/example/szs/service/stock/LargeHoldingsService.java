package com.example.szs.service.stock;

import com.example.szs.domain.stock.LargeHoldingsDetailEntity;
import com.example.szs.domain.stock.LargeHoldingsEntity;
import com.example.szs.model.dto.largeHoldings.LHResponseDTO;
import com.example.szs.model.dto.largeHoldings.LargeHoldingsDTO;
import com.example.szs.model.dto.largeHoldings.LargeHoldingsDetailDTO;
import com.example.szs.model.dto.MessageDto;
import com.example.szs.model.dto.page.PageDTO;
import com.example.szs.model.dto.largeHoldings.LargeHoldingsStkrtDTO;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.model.eNum.redis.ChannelType;
import com.example.szs.model.eNum.stock.SellOrBuyType;
import com.example.szs.model.queryDSLSearch.LargeHoldingStkrtSearchCondition;
import com.example.szs.model.queryDSLSearch.LargeHoldingsDetailSearchCondition;
import com.example.szs.model.queryDSLSearch.LargeHoldingsSearchCondition;
import com.example.szs.module.ApiResponse;
import com.example.szs.module.redis.RedisPublisher;
import com.example.szs.module.stock.LargeHoldings;
import com.example.szs.module.stock.WebCrawling;
import com.example.szs.repository.stock.LargeHoldingsDetailRepositoryCustom;
import com.example.szs.repository.stock.LargeHoldingsRepository;
import com.example.szs.repository.stock.LargeHoldingsRepositoryCustom;
import com.example.szs.repository.stock.LargeHoldingsStkrtRepositoryCustom;
import com.example.szs.utils.jpa.EntityToDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class LargeHoldingsService {
    @Value("${dart.uri.base}")
    private String baseUri;
    @Value("${dart.uri.largeHoldings}")
    private String path;
    @Value("${corp.code.key}")
    private String corpCodeKey;
//    @Value("${corp.code.value}")
//    private String corpCodeValue;
    @Value("${dart.key}")
    private String dartKey;
    @Value("${dart.value}")
    private String dartValue;

    private final LargeHoldingsRepository largeHoldingsRepository;
    private final LargeHoldingsRepositoryCustom largeHoldingsRepositoryCustom;
    private final LargeHoldingsDetailRepositoryCustom largeHoldingsDetailRepositoryCustom;
    private final LargeHoldingsStkrtRepositoryCustom largeHoldingsStkrtRepositoryCustom;

    private final RedisPublisher redisPublisher;
    private final WebCrawling webCrawling;
    private final ApiResponse apiResponse;
    private final LargeHoldings largeHoldings;

    @Transactional
//    @Scheduled(cron = "0 0 9 * * ?")
    public void insertData(String corpCodeValue) {
        WebClient webClient = WebClient.builder()
                                       .baseUrl(baseUri)
                                       .build();

        Mono<LHResponseDTO> lhResponseDtoMono = webClient.get()
                                                         .uri(uriBuilder -> uriBuilder.path(path)
                                                                                      .queryParam(dartKey, dartValue)
                                                                                      .queryParam(corpCodeKey, corpCodeValue)
                                                                                      .build()).retrieve().bodyToMono(LHResponseDTO.class);

        LHResponseDTO lhResponseDTO = lhResponseDtoMono.block();
        if (lhResponseDTO == null) {
            return;
        }

        List<LargeHoldingsEntity> largeHoldingsEntityList = lhResponseDTO.toEntity();

        Optional<LargeHoldingsDTO> optionalLargeHoldingsDTO = largeHoldingsRepositoryCustom.findLatestRecordBy(LargeHoldingsSearchCondition.builder()
                                                                                                                                           .corpCode(corpCodeValue)
                                                                                                                                           .orderColumn(LargeHoldingsEntity.Fields.rceptNo)
                                                                                                                                           .isDescending(true)
                                                                                                                                           .build());
        if (optionalLargeHoldingsDTO.isEmpty()) {
            largeHoldingsRepository.saveAll(largeHoldingsEntityList);
            if (!CollectionUtils.isEmpty(largeHoldingsEntityList)) {

                List<LargeHoldingsDTO> requestBody = largeHoldingsEntityList.stream()
                                                                            .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsDTO.class).stream())
                                                                            .collect(Collectors.toList());
                this.updateScraping(requestBody);
            }
            return;
        }

        LargeHoldingsEntity findLatestRecord = EntityToDtoMapper.mapEntityToDto(optionalLargeHoldingsDTO.get(), LargeHoldingsEntity.class).get();

        Comparator<LargeHoldingsEntity> comparator = (o1, o2) -> {
            String rceptNo_o1 = o1.getRceptNo();
            String rceptNo_o2 = o2.getRceptNo();

            if (!StringUtils.hasText(rceptNo_o1)) {
                return -1;
            }

            if (!StringUtils.hasText(rceptNo_o2)) {
                return 1;
            }

            return rceptNo_o1.compareTo(rceptNo_o2);
        };

        largeHoldingsEntityList.sort(comparator);

        int findIndex = Collections.binarySearch(largeHoldingsEntityList, findLatestRecord, comparator);

        List<LargeHoldingsEntity> insertEntity = largeHoldingsEntityList.subList(findIndex + 1, largeHoldingsEntityList.size());
        largeHoldingsRepository.saveAll(insertEntity);

        // ############ 대주주 세부 내용 웹 크롤링 ############ [start]
        if (!CollectionUtils.isEmpty(largeHoldingsEntityList)) {
            List<LargeHoldingsDTO> requestBody = largeHoldingsEntityList.stream()
                                                                        .map(entity -> EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsDTO.class))
                                                                        .flatMap(Optional::stream)
                                                                        .toList();
            this.updateScraping(requestBody);
        }
        // ############ 대주주 세부 내용 웹 크롤링 ############ [end]

        if (insertEntity.isEmpty()) {
            return;
        }

        List<LargeHoldingsDTO> largeHoldingsDTOList = new ArrayList<>();
        for (LargeHoldingsEntity entity : insertEntity) {
            Optional<LargeHoldingsDTO> dtoOptional = EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsDTO.class);
            dtoOptional.ifPresent(largeHoldingsDTOList::add);
        }

        String message = LargeHoldingsDTO.getMessage("삼상전자", largeHoldingsDTOList);
        MessageDto messageDto = MessageDto.builder()
                                          .message(message)
                                          .channelType(ChannelType.STOCK_CHANGE_NOTIFY_LARGE_HOLDINGS)
                                          .build();

        redisPublisher.pubMsgChannel(messageDto);
    }

    public <T> ResponseEntity<?> getSearchPageLargeHoldingsDetail(LargeHoldingsDetailSearchCondition condition, Pageable pageable) {
        Page<LargeHoldingsDetailDTO> page = largeHoldingsDetailRepositoryCustom.searchPage(condition, pageable);

        PageDTO pageDTO = PageDTO.builder()
                                 .content(page.getContent())
                                 .totalElements(page.getTotalElements())
                                 .totalPages(page.getTotalPages())
                                 .build();

        return apiResponse.makeResponse(ResStatus.SUCCESS, pageDTO);
    }

    @Transactional
    public ResponseEntity<?> updateScraping(List<LargeHoldingsDTO> largeHoldingsDTOList) {
        for (LargeHoldingsDTO dto : largeHoldingsDTOList) {
            try {
                List<LargeHoldingsDetailDTO> largeHoldingsDetailDTOList = webCrawling.getLargeHoldingsDetailCrawling(dto.getRceptNo(), dto.getCorpCode(), dto.getCorpName());
                largeHoldingsDetailRepositoryCustom.saveLargeHoldingsDetail(largeHoldingsDetailDTOList);

                // TODO : LargeHoldingsDetail <-> LargeHoldingsStkrt 일대일 관계 매핑해야함.
                List<LargeHoldingsStkrtDTO> largeHoldingsStkrtDTOList = webCrawling.getLargeHoldingsStkrtCrawling(dto.getRceptNo(), dto.getCorpCode(), dto.getCorpName());
                largeHoldingsStkrtRepositoryCustom.saveLargeHoldingsStkrt(largeHoldingsStkrtDTOList);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(dto.getRceptNo(), dto.getCorpCode(), dto.getCorpName());
            }

        }
        return apiResponse.makeResponse(ResStatus.SUCCESS);
    }

    public ResponseEntity<?> getLargeHoldingsStockRatio(LargeHoldingStkrtSearchCondition condition) {
        List<LargeHoldingsStkrtDTO> findLargeHoldingsStockRatioList = largeHoldingsStkrtRepositoryCustom.getLargeHoldingsStockRatio(condition.toBuilder()
                                                                                                                                             .limit(1L)
                                                                                                                                             .build());

        List<LargeHoldingsStkrtDTO> filteredStkrtExcNullOrInit = findLargeHoldingsStockRatioList.stream()
                                                                                                .filter(dto -> dto.getStkrt() != null && dto.getStkrt() != 0.0F)
                                                                                                .collect(Collectors.toList());
        return apiResponse.makeResponse(ResStatus.SUCCESS, filteredStkrtExcNullOrInit);
    }

    public ResponseEntity<?> getLargeHoldingsMonthlyTradeCnt(String corpCode) {
        assert (corpCode != null) : "corpCode not null";

        // 매월 매도건수
        LargeHoldingsDetailDTO.SellOrBuyMonthlyCountResponse sell = LargeHoldingsDetailDTO.SellOrBuyMonthlyCountResponse.builder()
                                                                                                                        .sellOrBuyType(SellOrBuyType.SELL.getCode())
                                                                                                                        .monthlyCountDTOList(largeHoldingsDetailRepositoryCustom.getLargeHoldingsMonthlyTradeCnt(corpCode, true))
                                                                                                                        .build();

        // 매월 매수건수
        LargeHoldingsDetailDTO.SellOrBuyMonthlyCountResponse buy = LargeHoldingsDetailDTO.SellOrBuyMonthlyCountResponse.builder()
                                                                                                                        .sellOrBuyType(SellOrBuyType.BUY.getCode())
                                                                                                                        .monthlyCountDTOList(largeHoldingsDetailRepositoryCustom.getLargeHoldingsMonthlyTradeCnt(corpCode, false))
                                                                                                                        .build();

        List<LargeHoldingsDetailDTO.SellOrBuyMonthlyCountResponse> responses = Arrays.asList(sell, buy);
        return apiResponse.makeResponse(ResStatus.SUCCESS, responses);
    }

    public ResponseEntity<?> getLargeHoldingsStockRatioTop5(String corpCode) {
        List<LargeHoldingsStkrtDTO> findLargeHoldingsStockRatioList = largeHoldingsStkrtRepositoryCustom.getLargeHoldingsStockRatio(LargeHoldingStkrtSearchCondition.builder()
                                                                                                                                                                    .corpCode(corpCode)
                                                                                                                                                                    .limit(1L)
                                                                                                                                                                    .build());

        List<LargeHoldingsStkrtDTO> top5List = findLargeHoldingsStockRatioList.size() > 5 ? findLargeHoldingsStockRatioList.stream().limit(5).collect(Collectors.toList()) : findLargeHoldingsStockRatioList;


        return apiResponse.makeResponse(ResStatus.SUCCESS, top5List);
    }

    public ResponseEntity<?> getLargeHoldingsTradeDtBy(String corpCode, String largeHoldingsName) {
        List<LargeHoldingsDetailDTO> largeHoldingsDetailDTOList = largeHoldingsDetailRepositoryCustom.getLargeHoldingsDetailDTOListBy(LargeHoldingsDetailSearchCondition.builder()
                                                                                                                                                                        .corpCodeEq(corpCode)
                                                                                                                                                                        .largeHoldingsNameEq(largeHoldingsName)
                                                                                                                                                                        .orderColumn(LargeHoldingsDetailEntity.Fields.tradeDt)
                                                                                                                                                                        .isDescending(false)
                                                                                                                                                                        .build());

        return apiResponse.makeResponse(ResStatus.SUCCESS, largeHoldingsDetailDTOList);
    }
}
