package com.example.szs.domain.stock;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Builder(toBuilder = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "large_holdings_stkrt")
@Getter
@FieldNameConstants
public class LargeHoldingsStkrtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @Column(name = "rcept_no", nullable = false)
    private String rceptNo; // 접수 번호

    @Column(name = "corp_code", nullable = false)
    private Long corpCode; // 회사코드

    @Column(name = "corp_name", nullable = false, length = 20)
    private String corpName; // 회사명

    @Column(name = "large_holdings_name", nullable = false, length = 50)
    private String largeHoldingsName; // 대주주 이름

    @Column(name = "birth_date_or_biz_reg_num", nullable = false, length = 20)
    private String birthDateOrBizRegNum; // 생년월일 또는 사업자등록번호 등

    @Column(name = "total_stock_amount")
    private Long totalStockAmount; // 전체 주식수

    @Column(name = "stkrt")
    private Float stkrt; //

    @Column(name = "reg_dt")
    private String regDt;
}
