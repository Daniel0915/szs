package com.example.szs.model.eNum.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum HeaderType {
    AUTH_HEADER("Authorization", "인증 헤더"),
    ;

    private final String code;
    private final String desc;
}
