package com.example.szs.service.stock;

import com.example.szs.domain.stock.LargeHoldingsEntity;
import com.example.szs.model.dto.LHResponseDTO;
import com.example.szs.repository.stock.LargeHoldingsRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@Slf4j
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

    public LargeHoldingsService(LargeHoldingsRepository largeHoldingsRepository) {
        this.largeHoldingsRepository    = largeHoldingsRepository;
    }

    public LHResponseDTO insertData() {
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
            return null;
        }

        List<LargeHoldingsEntity> largeHoldingsEntityList = lhResponseDTO.toEntity();

        return lhResponseDtoMono.block();

    }

}
