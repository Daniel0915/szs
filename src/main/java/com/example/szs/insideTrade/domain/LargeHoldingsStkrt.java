package com.example.szs.insideTrade.domain;

import com.example.szs.utils.time.TimeUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "large_holdings_stkrt")
@Getter
@FieldNameConstants
public class LargeHoldingsStkrt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @Column(name = "rcept_no", nullable = false)
    private String rceptNo; // 접수 번호

    @Column(name = "corp_code", nullable = false)
    private String corpCode; // 회사코드

    @Column(name = "corp_name")
    private String corpName; // 회사명

    @Column(name = "large_holdings_name")
    private String largeHoldingsName; // 대주주 이름

    @Column(name = "birth_date_or_biz_reg_num")
    private String birthDateOrBizRegNum; // 생년월일 또는 사업자등록번호 등

    @Column(name = "total_stock_amount")
    private Long totalStockAmount; // 전체 주식수

    @Column(name = "stkrt")
    private Float stkrt;

    @Column(name = "reg_dt")
    private String regDt;

    private LargeHoldingsStkrt(String rceptNo, String corpCode, String corpName, String largeHoldingsName, String birthDateOrBizRegNum, Long totalStockAmount, Float stkrt) {
        this.rceptNo = rceptNo;
        this.corpCode = corpCode;
        this.corpName = corpName;
        this.largeHoldingsName = largeHoldingsName;
        this.birthDateOrBizRegNum = birthDateOrBizRegNum;
        this.totalStockAmount = totalStockAmount;
        this.stkrt = stkrt;
        this.regDt = TimeUtil.nowTime("yyyyMMddHHmmss");
    }

    public static LargeHoldingsStkrt create(String rceptNo, String corpCode, String corpName, String largeHoldingsName, String birthDateOrBizRegNum, Long totalStockAmount, Float stkrt) {
        return new LargeHoldingsStkrt(rceptNo, corpCode, corpName, largeHoldingsName, birthDateOrBizRegNum, totalStockAmount, stkrt);
    }
}
