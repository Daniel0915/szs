package com.example.szs.domain.tax;

import com.example.szs.eNum.IncomeDeductionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "INCOME_DEDUCTION")
@Getter
public class IncomeDeduction {
    @Id @GeneratedValue
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAX_INFO_SEQ")
    private TaxInfo taxInfo;

    @Enumerated(EnumType.STRING)
    private IncomeDeductionType type;

    private BigDecimal amount;

    private String yearMonth;
}
