package com.example.szs.model.eNum.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PubType {
    STOCK_CHANGE_NOTIFY_LARGE_HOLDINGS("지분 공시 변동 - 대량보유 상황보고"),
    STOCK_CHANGE_EXECOWNERSHIP("지분 공시 변동 - 지분 공시 변동 - 대량보유 상황보고")
    ;

    private final String desc;
}
