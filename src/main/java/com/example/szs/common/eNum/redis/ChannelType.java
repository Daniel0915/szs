package com.example.szs.common.eNum.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum ChannelType {
    STOCK_CHANGE_NOTIFY_LARGE_HOLDINGS("지분 공시 변동 - 대량보유 상황보고"),
    STOCK_CHANGE_EXECOWNERSHIP("지분 공시 변동 - 지분 공시 변동 - 대량보유 상황보고")
    ;


    private final String desc;

    public static ChannelType findChannelTypeOrNull(String channelTypeStr) {
        ChannelType[] channelTypes = ChannelType.values();
        for (ChannelType channelType : channelTypes) {
            if (Objects.equals(channelType.name(), channelTypeStr)) {
                return channelType;
            }
        }
        return null;
    }
}
