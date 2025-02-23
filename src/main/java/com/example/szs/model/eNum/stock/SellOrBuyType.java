package com.example.szs.model.eNum.stock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SellOrBuyType {
    SELL("sell", "매도"),
    BUY("buy", "매수")
    ;

    private final String code;
    private final String desc;
}
