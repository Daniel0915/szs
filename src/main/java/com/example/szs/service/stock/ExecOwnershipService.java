package com.example.szs.service.stock;

import com.example.szs.domain.stock.ExecOwnershipEntity;
import com.example.szs.model.dto.execOwnership.EOResponseDTO;
import com.example.szs.model.dto.execOwnership.ExecOwnershipDTO;
import com.example.szs.model.dto.MessageDto;
import com.example.szs.model.dto.execOwnership.ExecOwnershipDetailDTO;
import com.example.szs.model.dto.page.PageDTO;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.model.eNum.redis.ChannelType;
import com.example.szs.model.queryDSLSearch.ExecOwnershipDetailSearchCondition;
import com.example.szs.model.queryDSLSearch.ExecOwnershipSearchCondition;
import com.example.szs.model.queryDSLSearch.LargeHoldingsDetailSearchCondition;
import com.example.szs.module.ApiResponse;
import com.example.szs.module.redis.RedisPublisher;
import com.example.szs.module.stock.WebCrawling;
import com.example.szs.repository.stock.ExecOwnershipDetailRepository;
import com.example.szs.repository.stock.ExecOwnershipDetailRepositoryCustom;
import com.example.szs.repository.stock.ExecOwnershipRepository;
import com.example.szs.repository.stock.ExecOwnershipRepositoryCustom;
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

import java.util.*;

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

    private final ExecOwnershipRepository execOwnershipRepository;
    private final ExecOwnershipRepositoryCustom execOwnershipRepositoryCustom;
    private final ExecOwnershipDetailRepositoryCustom execOwnershipDetailRepositoryCustom;
    private final ApiResponse apiResponse;
    private final RedisPublisher redisPublisher;
    private final WebCrawling webCrawling;

    @Transactional
    public void insertData(String corpCodeValue) {
        WebClient webClient = WebClient.builder()
                                       .baseUrl(baseUri)
                                       .build();

        Mono<EOResponseDTO> eoResponseDTOMono = webClient.get()
                                                         .uri(uriBuilder -> uriBuilder.path(path)
                                                                                      .queryParam(dartKey, dartValue)
                                                                                      .queryParam(corpCodeKey, corpCodeValue)
                                                                                      .build()).retrieve().bodyToMono(EOResponseDTO.class);

        EOResponseDTO eoResponseDTO = eoResponseDTOMono.block();
        if (eoResponseDTO == null) {
            return;
        }

        List<ExecOwnershipEntity> execOwnershipEntityList = eoResponseDTO.toEntity();

        Optional<ExecOwnershipDTO> optionalExecOwnershipDTO = execOwnershipRepositoryCustom.findLatestRecordBy(ExecOwnershipSearchCondition.builder()
                                                                                                                                           .corpCode(corpCodeValue)
                                                                                                                                           .orderColumn(ExecOwnershipEntity.Fields.rceptNo)
                                                                                                                                           .isDescending(true)
                                                                                                                                           .build());
        if (optionalExecOwnershipDTO.isEmpty()) {
            execOwnershipRepository.saveAll(execOwnershipEntityList);
            if (!CollectionUtils.isEmpty(execOwnershipEntityList)) {
                List<ExecOwnershipDetailDTO> insertExecExecOwnershipDetailDTOList = new ArrayList<>();
                for (ExecOwnershipEntity entity :execOwnershipEntityList) {
                    insertExecExecOwnershipDetailDTOList.addAll(webCrawling.getExecOwnershipDetailCrawling(
                            entity.getRceptNo(),
                            entity.getCorpCode(),
                            entity.getCorpName(),
                            entity.getRepror(),
                            entity.getIsuExctvRgistAt(),
                            entity.getIsuExctvOfcps(),
                            entity.getIsuMainShrholdr()
                    ));
                }
                execOwnershipDetailRepositoryCustom.saveAll(insertExecExecOwnershipDetailDTOList);
            }
            return;
        }

        ExecOwnershipEntity findLatestRecord = EntityToDtoMapper.mapEntityToDto(optionalExecOwnershipDTO.get(), ExecOwnershipEntity.class).get();

        Comparator<ExecOwnershipEntity> comparator = (o1, o2) -> {
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

        execOwnershipEntityList.sort(comparator);

        int findIndex = Collections.binarySearch(execOwnershipEntityList, findLatestRecord, comparator);

        List<ExecOwnershipEntity> insertEntity = execOwnershipEntityList.subList(findIndex + 1, execOwnershipEntityList.size());

        if (insertEntity.isEmpty()) {
            return;
        }

        execOwnershipRepository.saveAll(insertEntity);

        List<ExecOwnershipDetailDTO> updateExecExecOwnershipDetailDTOList = new ArrayList<>();
        for (ExecOwnershipEntity entity : insertEntity) {
            updateExecExecOwnershipDetailDTOList.addAll(webCrawling.getExecOwnershipDetailCrawling(entity.getRceptNo(),
                    entity.getCorpCode(),
                    entity.getCorpName(),
                    entity.getRepror(),
                    entity.getIsuExctvRgistAt(),
                    entity.getIsuExctvOfcps(),
                    entity.getIsuMainShrholdr()));
        }
        execOwnershipDetailRepositoryCustom.saveAll(updateExecExecOwnershipDetailDTOList);

        List<ExecOwnershipDTO> execOwnershipDTOList = new ArrayList<>();
        for (ExecOwnershipEntity entity : insertEntity) {
            Optional<ExecOwnershipDTO> dtoOptional = EntityToDtoMapper.mapEntityToDto(entity, ExecOwnershipDTO.class);
            dtoOptional.ifPresent(execOwnershipDTOList::add);
        }

        String message = ExecOwnershipDTO.getMessage("삼상전자", execOwnershipDTOList);
        MessageDto messageDto = MessageDto.builder()
                                          .message(message)
                                          .channelType(ChannelType.STOCK_CHANGE_EXECOWNERSHIP)
                                          .build();
        redisPublisher.pubMsgChannel(messageDto);
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
}
