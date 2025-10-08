package com.example.szs.insideTrade.domain;

import com.example.szs.insideTrade.domain.embedded.Time;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_push_subs")
@Getter
public class UserPushSubs {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "channel_type")
    private String channelType;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "regDt", column = @Column(name = "reg_dt")),
            @AttributeOverride(name = "modDt", column = @Column(name = "mod_dt"))
    })
    private Time time;
}
