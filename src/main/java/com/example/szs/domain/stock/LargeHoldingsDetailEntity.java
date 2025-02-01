package com.example.szs.domain.stock;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Builder(toBuilder = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "large_holdings_detail")
@Getter
@FieldNameConstants
@ToString
public class LargeHoldingsDetailEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rcept_no")
    private Long seq;

    @Column(name = "rcept_no", nullable = false)
    private Long rceptNo; // 접수 번호

    @Column(name = "corp_code", nullable = false)
    private Long corpCode; // 회사코드

    @Column(name = "corp_name", nullable = false, length = 20)
    private String corpName; // 회사명

    @Column(name = "large_holdings_name", nullable = false, length = 50)
    private String largeHoldingsName; // 대주주 이름

    @Column(name = "birth_date_or_biz_reg_num", nullable = false, length = 20)
    private String birthDateOrBizRegNum; // 생년월일 또는 사업자등록번호 등

    @Column(name = "trade_dt", nullable = false, length = 8)
    private String tradeDt; // 거래일

    @Column(name = "trade_reason", nullable = false, length = 20)
    private String tradeReason; // 매매 사유

    @Column(name = "stock_type", nullable = false, length = 20)
    private String stockType; // 주식 종류

    @Column(name = "before_stock_amount", nullable = false)
    private Long beforeStockAmount; // 주식 변동전

    @Column(name = "chagne_stock_amount", nullable = false)
    private Long changeStockAmount; // 주식 증감

    @Column(name = "after_stock_amount", nullable = false)
    private Long afterStockAmount; // 주식 변동후

    @Column(name = "unit_stock_price", nullable = false)
    private Long unitStockPrice; // 주식 단가

    @Column(name = "currency_type", nullable = false, length = 5)
    private String currencyType; // 주식 단가 통화

    @Column(name = "total_stock_price", nullable = false)
    private Long totalStockPrice; // 전체 주식 취득 / 처분 단가
}
