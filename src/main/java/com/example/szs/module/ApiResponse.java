package com.example.szs.module;

import com.example.szs.model.eNum.ResStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ApiResponse {
    private  <T, E> ResponseEntity<?> get(String sCode, @Nullable String sMsg, @Nullable T data) {
        return new ResponseEntity<>(SucceededBody.builder()
                                                 .sCode(sCode)
                                                 .sMsg(sMsg)
                                                 .data(data)
                                                 .build(),
                HttpStatus.OK);
    }
    public <T> ResponseEntity<?> makeResponse(ResStatus resStatus, T data) {
        return get(resStatus.getSCode(), resStatus.getSMsg(), data);
    }

    // 실패했을 경우
    public <T> ResponseEntity<?> makeResponse(ResStatus resStatus) {
        return get(resStatus.getSCode(), resStatus.getSMsg(), null);
    }

    @Builder
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SucceededBody<T> {

        @JsonProperty("sCode")
        private String sCode;
        @JsonProperty("sMsg")
        private String sMsg;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private T data;
    }
}
