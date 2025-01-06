package com.example.szs.model.dto;

import com.example.szs.config.json.NullToEmptySerializer;
import com.example.szs.model.eNum.redis.ChannelType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto implements Serializable {
    @JsonIgnore
    private static final long serialVersionUID = 1L;

    // 전송할 메시지 내용
    private String message;
    // channel
    private ChannelType channelType;
}
