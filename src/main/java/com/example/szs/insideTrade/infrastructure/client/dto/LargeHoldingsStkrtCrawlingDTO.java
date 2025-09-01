package com.example.szs.insideTrade.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LargeHoldingsStkrtCrawlingDTO {
    private Long seq;
    private String rceptNo;
    private String corpCode;
    private String corpName;
    private String largeHoldingsName; // 대주주 이름
    private String birthDateOrBizRegNum; // 생년월일 또는 사업자 등록번호
    private Long totalStockAmount; // 전체 주식수
    private Float stkrt; // 지분비율
    private String regDt;
}
