package com.example.szs.service.stock;

import com.example.szs.repository.stock.LargeHoldingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LargeHoldingsService {
    @Value("${dart.uri.base}")
    private String baseUri;
    @Value("${uri.largeHoldings}")
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

    public void insertData() {
        Object response = WebClient.create(baseUri)
                                   .get()
                                   .uri(uriBuilder ->
                                           uriBuilder.path(path)
                                                     .queryParam(corpCodeKey, corpCodeValue)
                                                     .queryParam(dartKey, dartValue)
                                                     .build()
                                   )
                                   .retrieve()
                                   .bodyToMono(Object.class);
    }




}
