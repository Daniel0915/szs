package com.example.szs.model.dto;

import com.example.szs.config.json.NullToEmptySerializer;
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
@JsonSerialize(using = NullToEmptySerializer.class)
public class MessageDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // 전송할 메시지 내용
    private String message;
    // 메시지 발신자
    private String sender;
    // 메세지 방 번호 || 타겟 Channel
    private String roomId;
}
