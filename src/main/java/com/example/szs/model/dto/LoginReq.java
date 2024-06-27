package com.example.szs.model.dto;

import com.example.szs.config.json.NullToEmptySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonSerialize(using = NullToEmptySerializer.class)
public class LoginReq {
    private String userId;
    private String password;
}

