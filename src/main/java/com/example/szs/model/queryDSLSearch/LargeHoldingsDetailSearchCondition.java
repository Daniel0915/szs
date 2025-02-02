package com.example.szs.model.queryDSLSearch;

import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LargeHoldingsDetailSearchCondition {
    private String orderColumn;
    private Boolean isDescending;

    // 거래일 trade_dt 범위
    private String tradeDtGoe;
    private String tradeDtLoe;


    // where 절 사용
    private String largeHoldingsName; // 대주주 이름
    private String birthDateOrBizRegNum; // 생년월일 또는 사업자등록번호 등
    private String tradeReason; // 매매 사유
    private String stockType; // 주식 종류
}
