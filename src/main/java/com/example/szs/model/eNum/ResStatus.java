package com.example.szs.model.eNum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public enum ResStatus {
    SUCCESS                     ("0000", "성공"),

    ERROR                       ("0001", "서비스 에러가 발생하였습니다."),
    ACCESS_KEY_ERROR            ("0002", "재로그인해주세요(회원정보가 없습니다)"),
    ACCESS_KEY_EXPIRE           ("0003", "ACCESS KEY 만료"), // token 이 없거나 헤더 형식 오류

    LOGIN_ERROR                 ("0004", "아이디 또는 비밀번호를 확인해주세요."),

    JOIN_ERROR                  ("0005", "회원가입에 실패했습니다."),
    JOIN_DUPLICATION_ERROR      ("0006", "이미 존재하는 회원입니다."),

    PERMISSION_DENIED           ("0007", "권한이 없습니다."),
    ;

    private final String sCode;
    private final String sMsg;

    public String getsCode() {
        return sCode;
    }

    public String getsMsg() {
        return sMsg;
    }
}
