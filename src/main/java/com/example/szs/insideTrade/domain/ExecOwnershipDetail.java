package com.example.szs.insideTrade.domain;

import com.example.szs.insideTrade.infrastructure.client.dto.ExecOwnershipDetailCrawlingDTO;
import com.example.szs.utils.time.TimeUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "exec_ownership_detail")
@Getter
@FieldNameConstants
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExecOwnershipDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", nullable = false)
    private Long seq;

    @Column(name = "rcept_no")
    private String rceptNo;

    @Column(name = "corp_code")
    private String corpCode;

    @Column(name = "corp_name")
    private String corpName;

    @Column(name = "exec_ownership_name")
    private String execOwnershipName;

    @Column(name = "isu_exctv_rgist_at")
    private String isuExctvRgistAt;

    @Column(name = "isu_exctv_ofcps")
    private String isuExctvOfcps;

    @Column(name = "isu_main_shrholdr")
    private String isuMainShrholdr;

    @Column(name = "trade_dt")
    private String tradeDt;

    @Column(name = "trade_reason")
    private String tradeReason;

    @Column(name = "stock_type")
    private String stockType;

    @Column(name = "before_stock_amount", nullable = false)
    private Long beforeStockAmount;

    @Column(name = "change_stock_amount", nullable = false)
    private Long changeStockAmount;

    @Column(name = "after_stock_amount", nullable = false)
    private Long afterStockAmount;

    @Column(name = "unit_stock_price")
    private String unitStockPrice;

    @Column(name = "reg_dt")
    private String regDt; // 등록일시

    private ExecOwnershipDetail(String rceptNo,
                                String corpCode,
                                String corpName,
                                String execOwnershipName,
                                String isuExctvRgistAt,
                                String isuExctvOfcps,
                                String isuMainShrholdr,
                                String tradeDt,
                                String tradeReason,
                                String stockType,
                                Long beforeStockAmount,
                                Long changeStockAmount,
                                Long afterStockAmount,
                                String unitStockPrice) {
        this.rceptNo = rceptNo;
        this.corpCode = corpCode;
        this.corpName = corpName;
        this.execOwnershipName = execOwnershipName;
        this.isuExctvRgistAt = isuExctvRgistAt;
        this.isuExctvOfcps = isuExctvOfcps;
        this.isuMainShrholdr = isuMainShrholdr;
        this.tradeDt = tradeDt;
        this.tradeReason = tradeReason;
        this.stockType = stockType;
        this.beforeStockAmount = beforeStockAmount;
        this.changeStockAmount = changeStockAmount;
        this.afterStockAmount = afterStockAmount;
        this.unitStockPrice = unitStockPrice;
        this.regDt = TimeUtil.nowTime("yyyyMMddHHmmss");
    }

    public static ExecOwnershipDetail create(ExecOwnershipDetailCrawlingDTO create) {
        return new ExecOwnershipDetail(
                create.getRceptNo(),
                create.getCorpCode(),
                create.getCorpName(),
                create.getExecOwnershipName(),
                create.getIsuExctvRgistAt(),
                create.getIsuExctvOfcps(),
                create.getIsuMainShrholdr(),
                create.getTradeDt(),
                create.getTradeReason(),
                create.getStockType(),
                create.getBeforeStockAmount(),
                create.getChangeStockAmount(),
                create.getAfterStockAmount(),
                create.getUnitStockPrice()
        );
    }
}
