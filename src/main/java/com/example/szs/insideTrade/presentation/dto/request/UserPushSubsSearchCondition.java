package com.example.szs.insideTrade.presentation.dto.request;

import com.example.szs.common.eNum.redis.ChannelType;
import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserPushSubsSearchCondition {
    private Long seq;
    private Long userId;
    private ChannelType channelType;
}
