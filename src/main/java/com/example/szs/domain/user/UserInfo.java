package com.example.szs.domain.user;

import com.example.szs.domain.embedded.Time;
import com.example.szs.domain.subscribe.UserPushSubs;
import com.example.szs.model.dto.user.UserInfoDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_info")
@Getter
public class UserInfo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_email", unique = true, length = 255)
    private String userEmail;

    @Column(name = "user_pwd", length = 100)
    private String userPwd;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "regDt", column = @Column(name = "reg_dt")),
            @AttributeOverride(name = "modDt", column = @Column(name = "mod_dt"))
    })
    private Time time;

    @OneToMany(mappedBy = "userInfo", fetch = FetchType.LAZY)
    private List<UserPushSubs> userPushSubsList = new ArrayList<>();

    public UserInfoDTO toDTO() {
        return UserInfoDTO.builder()
                          .userId       (userId)
                          .userEmail    (userEmail)
                          .userPwd      (userPwd)
                          .regDt        (time == null ? "" : time.getRegDt().toString())
                          .modDt        (time == null ? "" : time.getModDt().toString())
                          .build();
    }
}
