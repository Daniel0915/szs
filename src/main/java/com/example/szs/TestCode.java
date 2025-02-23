package com.example.szs;

import com.example.szs.domain.stock.LargeHoldingsEntity;
import com.example.szs.model.dto.largeHoldings.LHResponseDTO;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestCode {
    public static void main(String[] args) {

        String baseUri = "https://opendart.fss.or.kr/api";
        String path = "/majorstock.json";
        String dartKey = "crtfc_key";
        String dartValue = "3298668f7dadcb0bde716342daa730b831ebd3f7";
        String corpCodeKey = "corp_code";
        String corpCodeValue = "00126380";



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


        LargeHoldingsEntity findLatestRecord =  LargeHoldingsEntity.builder()
                                                                   .rceptNo("20241025000551")
                                                                   .build();



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

        for (LargeHoldingsEntity entity : insertEntity) {
            System.out.println("entity = " + entity.getRceptNo());
        }
    }
}
