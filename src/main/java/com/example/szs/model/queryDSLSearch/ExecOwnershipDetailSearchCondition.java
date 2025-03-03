package com.example.szs.model.queryDSLSearch;

import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExecOwnershipDetailSearchCondition {
    private String orderColumn;
    private Boolean isDescending;

    // 거래일 trade_dt 범위
    private String tradeDtGoe;
    private String tradeDtLoe;

    // 거래량 changeStockAmount 범위
    private Long changeStockAmountGoe;
    private Long changeStockAmountLoe;
    private Long changeStockAmountGt;
    private Long changeStockAmountLt;

    // 평단가 unitStockPrice 범위
    private Long unitStockPriceGoe;
    private Long unitStockPriceLoe;

    // 보유주식 afterStockAmount 범위
    private Long afterStockAmountGoe;
    private Long afterStockAmountLoe;

    // where eq 절 사용
    private String execOwnershipNameEq; // 임원 이름
    private String tradeReasonEq; // 매매 사유
    private String stockTypeEq; // 주식 종류
    private String corpCodeEq; // 회사코드

    // where contains
    private String execOwnershipNameContains; // 임원 이름
    private String tradeReasonContains; // 매매 사유
    private String stockTypeContains; // 주식 종류

    // limit
    private Long limit;

}
