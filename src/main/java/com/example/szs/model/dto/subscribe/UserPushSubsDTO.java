package com.example.szs.model.dto.subscribe;

import com.example.szs.domain.user.UserInfo;
import com.example.szs.model.dto.user.UserInfoDTO;
import com.example.szs.model.eNum.redis.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserPushSubsDTO {
    private Long seq;
    private UserInfoDTO userInfo;
    private String channelType;
    private String regDt;
    private String modDt;
}
