package com.example.szs.model.dto.user;

import com.example.szs.domain.subscribe.UserPushSubs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Long userId;
    private String userEmail;
    private String userPwd;
    private String regDt;
    private String modDt;
}
