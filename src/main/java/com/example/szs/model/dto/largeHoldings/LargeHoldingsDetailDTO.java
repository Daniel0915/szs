package com.example.szs.model.dto.largeHoldings;

import com.example.szs.config.json.NullToEmptySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = NullToEmptySerializer.class)
@FieldNameConstants
public class LargeHoldingsDetailDTO {
    private Long seq;
    private String rceptNo; // 접수 번호
    private String corpCode; // 회사코드
    private String corpName; // 회사명
    private String largeHoldingsName; // 대주주 이름
    private String birthDateOrBizRegNum; // 생년월일 또는 사업자등록번호 등
    private String tradeDt; // 거래일
    private String tradeReason; // 매매 사유
    private String stockType; // 주식 종류
    private Long beforeStockAmount; // 주식 변동전
    private Long changeStockAmount; // 주식 증감
    private Long afterStockAmount; // 주식 변동후
    private Long unitStockPrice; // 주식 단가
    private String currencyType; // 주식 단가 통화
    private Long totalStockPrice; // 전체 주식 취득 / 처분 단가
    private String regDt;

    @Getter
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldNameConstants
    public static class MonthlyCountDTO {
        private String month;
        private Long count;
    }


    @Getter
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldNameConstants
    public static class SellOrBuyMonthlyCountResponse {
        private String sellOrBuyType;
        private List<MonthlyCountDTO> monthlyCountDTOList;
    }


    @Getter
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldNameConstants
    public static class TopStockDetailDTO {
        private String corpCode; // 회사코드
        private String corpName; // 회사이름
        private Long totalStockAmount;
    }

    @Getter
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldNameConstants
    public static class SellOrBuyTop5StockResponse {
        private String sellOrBuyType;
        private List<TopStockDetailDTO> top5StockDetailDTOList;
    }
}
