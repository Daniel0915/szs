package com.example.szs.model.eNum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IncomeDeductionType {
    G("국민연금"),
    S("신용카드");
    private String description;
}
