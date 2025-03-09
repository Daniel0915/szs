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
@Table(name = "exec_ownership_detail")
@Getter
@FieldNameConstants
public class ExecOwnershipDetailEntity {
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
}
