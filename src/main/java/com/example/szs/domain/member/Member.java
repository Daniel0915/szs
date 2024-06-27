package com.example.szs.domain.member;

import com.example.szs.domain.embedded.Time;
import com.example.szs.domain.tax.TaxInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER")
@Getter
public class Member {
    @Id @GeneratedValue
    private Long seq;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "password")
    @JsonIgnore
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "reg_no")
    private String regNo;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "regDt", column = @Column(name = "reg_dt")),
            @AttributeOverride(name = "modDt", column = @Column(name = "mod_dt"))
    })
    private Time time;
}
