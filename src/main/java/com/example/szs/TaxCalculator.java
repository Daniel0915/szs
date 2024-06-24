package com.example.szs;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class TaxCalculator {
    private BigDecimal totalIncome; // 종합소득금액
    private BigDecimal totalIncomeDeductions; // 총 소득공제
    private BigDecimal taxableIncome; // 과세표준
    private BigDecimal calculatedTax; // 산출세액
    private BigDecimal finalTax; // 결정세액
    private BigDecimal taxDeduction; // 세액공제

    public TaxCalculator(BigDecimal totalIncome, BigDecimal totalIncomeDeductions, BigDecimal taxDeduction) {
        this.totalIncome = totalIncome;
        this.totalIncomeDeductions = totalIncomeDeductions;
        this.taxDeduction = taxDeduction;
        this.taxableIncome = this.totalIncome.subtract(this.totalIncomeDeductions);
    }

    public void calculatedTax() {
        BigDecimal intermediateValue;
        if (taxableIncome.compareTo(new BigDecimal("14000000")) <= 0) {
            intermediateValue = taxableIncome.multiply(new BigDecimal("0.06"));
            calculatedTax = intermediateValue.setScale(0, RoundingMode.HALF_UP);
        } else if (taxableIncome.compareTo(new BigDecimal("50000000")) <= 0) {
            intermediateValue = new BigDecimal("840000")
                    .add(taxableIncome.subtract(new BigDecimal("14000000"))
                                      .multiply(new BigDecimal("0.15"))
                                      .setScale(0, RoundingMode.HALF_UP));
            calculatedTax = intermediateValue.setScale(0, RoundingMode.HALF_UP);
        } else if (taxableIncome.compareTo(new BigDecimal("88000000")) <= 0) {
            intermediateValue = new BigDecimal("6240000")
                    .add(taxableIncome.subtract(new BigDecimal("50000000"))
                                      .multiply(new BigDecimal("0.24"))
                                      .setScale(0, RoundingMode.HALF_UP));
            calculatedTax = intermediateValue.setScale(0, RoundingMode.HALF_UP);
        } else if (taxableIncome.compareTo(new BigDecimal("150000000")) <= 0) {
            intermediateValue = new BigDecimal("15360000")
                    .add(taxableIncome.subtract(new BigDecimal("88000000"))
                                      .multiply(new BigDecimal("0.35"))
                                      .setScale(0, RoundingMode.HALF_UP));
            calculatedTax = intermediateValue.setScale(0, RoundingMode.HALF_UP);
        } else if (taxableIncome.compareTo(new BigDecimal("300000000")) <= 0) {
            intermediateValue = new BigDecimal("37060000")
                    .add(taxableIncome.subtract(new BigDecimal("150000000"))
                                      .multiply(new BigDecimal("0.38"))
                                      .setScale(0, RoundingMode.HALF_UP));
            calculatedTax = intermediateValue.setScale(0, RoundingMode.HALF_UP);
        } else if (taxableIncome.compareTo(new BigDecimal("500000000")) <= 0) {
            intermediateValue = new BigDecimal("94060000")
                    .add(taxableIncome.subtract(new BigDecimal("300000000"))
                                      .multiply(new BigDecimal("0.40"))
                                      .setScale(0, RoundingMode.HALF_UP));
            calculatedTax = intermediateValue.setScale(0, RoundingMode.HALF_UP);
        } else if (taxableIncome.compareTo(new BigDecimal("1000000000")) <= 0) {
            intermediateValue = new BigDecimal("174060000")
                    .add(taxableIncome.subtract(new BigDecimal("500000000"))
                                      .multiply(new BigDecimal("0.42"))
                                      .setScale(0, RoundingMode.HALF_UP));
            calculatedTax = intermediateValue.setScale(0, RoundingMode.HALF_UP);
        } else {
            intermediateValue = new BigDecimal("384060000")
                    .add(taxableIncome.subtract(new BigDecimal("1000000000"))
                                      .multiply(new BigDecimal("0.45"))
                                      .setScale(0, RoundingMode.HALF_UP));
            calculatedTax = intermediateValue.setScale(0, RoundingMode.HALF_UP);
        }

        this.finalTax = this.calculatedTax.subtract(this.taxDeduction).setScale(0, RoundingMode.HALF_UP);
    }
}
