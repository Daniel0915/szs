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

    // where eq 절 사용
    private String largeHoldingsNameEq; // 대주주 이름
    private String birthDateOrBizRegNumEq; // 생년월일 또는 사업자등록번호 등
    private String tradeReasonEq; // 매매 사유
    private String stockTypeEq; // 주식 종류

    // where contains
    private String largeHoldingsNameContains; // 대주주 이름
    private String birthDateOrBizRegNumEqContains; // 생년월일 또는 사업자등록번호 등
    private String tradeReasonContains; // 매매 사유
    private String stockTypeContains; // 주식 종류
}
