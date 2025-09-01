package com.example.szs.insideTrade.infrastructure.push.dto;

import com.example.szs.model.eNum.redis.ChannelType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    // 전송할 메시지 내용
    private String message;
    private String corpCode;
    // channel
    private ChannelType channelType;
}
