package com.example.szs.domain.subscribe;

import com.example.szs.domain.embedded.Time;
import com.example.szs.domain.user.UserInfo;
import com.example.szs.model.dto.subscribe.UserPushSubsDTO;
import jakarta.persistence.*;
import lombok.*;

@Builder(toBuilder = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_push_subs")
@Getter
public class UserPushSubs {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    // 다대일 양방향(UserPushSubs <-> UserPushSubs)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo userInfo;

    @Column(name = "channel_type")
    private String channelType;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "regDt", column = @Column(name = "reg_dt")),
            @AttributeOverride(name = "modDt", column = @Column(name = "mod_dt"))
    })
    private Time time;


    public UserPushSubsDTO toDTO() {
        return UserPushSubsDTO.builder()
                              .seq              (seq)
                              .userInfo         (userInfo.toDTO())
                              .channelType      (channelType)
                              .regDt            (time == null ? "" : time.getRegDt().toString())
                              .modDt            (time == null ? "" : time.getModDt().toString())
                              .build();
    }
}
