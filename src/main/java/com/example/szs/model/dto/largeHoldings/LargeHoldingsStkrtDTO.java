package com.example.szs.model.dto.largeHoldings;

import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LargeHoldingsStkrtDTO {
    private Long seq;
    private String rceptNo;
    private Long corpCode;
    private String corpName;
    private String largeHoldingsName; // 대주주 이름
    private String birthDateOrBizRegNum; // 생년월일 또는 사업자 등록번호
    private Long totalStockAmount; // 전체 주식수
    private Float stkrt; // 지분비율
    private String regDt;
}
