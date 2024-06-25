package com.example.szs.domain.member;

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
    // TODO : 암호화화여 DB 저장

    @Column(name = "password")
    @JsonIgnore
    private String password;

    @Column(name = "name")
    private String name;

    // TODO : 암호화화여 DB 저장
    @Column(name = "reg_no")
    private String regNo;
    // TODO : 임베디드 타입으로 -> reg_date, mod_data 추가

    @OneToMany(mappedBy = "member")
    private List<TaxInfo> taxInfoList = new ArrayList<>();
}
