package com.example.szs.domain.tax;

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

// TODO : toBuilder 넣을지 고민 -> builder 해도 jpa 영속성을 가지고 있지 않음.
@Builder
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
}
