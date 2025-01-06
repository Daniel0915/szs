package com.example.szs.service.stock;

import com.example.szs.domain.stock.LargeHoldingsEntity;
import com.example.szs.model.dto.LHResponseDTO;
import com.example.szs.model.dto.LargeHoldingsDTO;
import com.example.szs.model.dto.MessageDto;
import com.example.szs.model.eNum.redis.ChannelType;
import com.example.szs.model.queryDSLSearch.LargeHoldingsSearchCondition;
import com.example.szs.module.redis.RedisPublisher;
import com.example.szs.repository.stock.LargeHoldingsRepository;
import com.example.szs.repository.stock.LargeHoldingsRepositoryCustom;
import com.example.szs.utils.jpa.EntityToDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

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
    @Value("${corp.code.value}")
    private String corpCodeValue;
    @Value("${dart.key}")
    private String dartKey;
    @Value("${dart.value}")
    private String dartValue;

    private final LargeHoldingsRepository largeHoldingsRepository;
    private final LargeHoldingsRepositoryCustom largeHoldingsRepositoryCustom;
    private final RedisPublisher redisPublisher;

    @Transactional
    @Scheduled(cron = "0 0 9 * * ?")
    public void insertData() {
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
                                                                                                                                           .corpCode(Long.valueOf(corpCodeValue))
                                                                                                                                           .orderColumn(LargeHoldingsEntity.Fields.rceptNo)
                                                                                                                                           .isDescending(true)
                                                                                                                                           .build());
        if (optionalLargeHoldingsDTO.isEmpty()) {
            largeHoldingsRepository.saveAll(largeHoldingsEntityList);
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
}
