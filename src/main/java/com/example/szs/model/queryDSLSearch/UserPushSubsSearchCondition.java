package com.example.szs.model.queryDSLSearch;

import com.example.szs.model.eNum.redis.ChannelType;
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
