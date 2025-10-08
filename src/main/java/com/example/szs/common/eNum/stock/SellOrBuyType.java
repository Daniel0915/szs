package com.example.szs.common.eNum.stock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SellOrBuyType {
    SELL("sell", "매도"),
    BUY("buy", "매수"),
    ALL("all", "매수+매도")
    ;

    private final String code;
    private final String desc;
}
