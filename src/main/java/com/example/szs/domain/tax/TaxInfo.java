package com.example.szs.domain.tax;

import com.example.szs.domain.embedded.Time;
import com.example.szs.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TAX_INFO")
@Getter
public class TaxInfo {
    @Id @GeneratedValue
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_SEQ")
    private Member member;

    // 조회년도
    @Column(name="\"YEAR\"")
    private int year;

    // 종합소득 금액
    @Column(name = "TOTAL_INCOME_AMOUNT")
    private BigDecimal totalInComeAmount;

    // 새액공제 금액
    @Column(name = "TAX_DEDUCTION_AMOUNT")
    private BigDecimal taxDeductionAmount;

    @OneToMany(mappedBy = "taxInfo")
    private List<IncomeDeduction> incomeDeductionList = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "regDt", column = @Column(name = "reg_dt")),
            @AttributeOverride(name = "modDt", column = @Column(name = "mod_dt"))
    })
    private Time time;




}
