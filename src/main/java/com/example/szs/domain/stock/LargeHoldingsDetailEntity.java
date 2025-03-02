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
public class LargeHoldingsDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", nullable = false)
    private Long seq;

    @Column(name = "rcept_no", length = 30)
    private String rceptNo; // 접수 번호

    @Column(name = "corp_code", columnDefinition = "varchar DEFAULT ''")
    private String corpCode; // 회사코드

    @Column(name = "corp_name", length = 20, columnDefinition = "varchar(20) DEFAULT ''")
    private String corpName; // 회사명

    @Column(name = "large_holdings_name", length = 50, columnDefinition = "varchar(50) DEFAULT ''")
    private String largeHoldingsName; // 대주주 이름

    @Column(name = "birth_date_or_biz_reg_num", length = 20, columnDefinition = "varchar(20) DEFAULT ''")
    private String birthDateOrBizRegNum; // 생년월일 또는 사업자등록번호

    @Column(name = "trade_dt", length = 20, columnDefinition = "varchar(20) DEFAULT ''")
    private String tradeDt; // 거래일

    @Column(name = "trade_reason", length = 20, columnDefinition = "varchar(20) DEFAULT ''")
    private String tradeReason; // 매매 사유

    @Column(name = "stock_type", length = 20, columnDefinition = "varchar(20) DEFAULT ''")
    private String stockType; // 주식 종류

    @Column(name = "before_stock_amount", nullable = false)
    private Long beforeStockAmount; // 주식 변동전

    @Column(name = "change_stock_amount", nullable = false)
    private Long changeStockAmount; // 주식 증감 (타이포 수정)

    @Column(name = "after_stock_amount", nullable = false)
    private Long afterStockAmount; // 주식 변동후

    @Column(name = "unit_stock_price", nullable = false)
    private Long unitStockPrice; // 주식 단가

    @Column(name = "currency_type", columnDefinition = "varchar DEFAULT ''")
    private String currencyType; // 주식 단가 통화

    @Column(name = "total_stock_price", nullable = false)
    private Long totalStockPrice; // 전체 주식 취득 / 처분 단가

    @Column(name = "reg_dt", length = 14, columnDefinition = "varchar(14) DEFAULT ''")
    private String regDt; // 등록일시
}
