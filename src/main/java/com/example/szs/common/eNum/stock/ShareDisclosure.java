package com.example.szs.common.eNum.stock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShareDisclosure {
    LARGE_HOLDINGS("largeHoldings", "대주주"),
    EXEC_OWNERSHIP("execOwnership", "임원 주주"),
    ;

    private final String code;
    private final String desc;
}
