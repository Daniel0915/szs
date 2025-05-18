package com.example.szs.utils.Response;

import com.example.szs.model.eNum.ResStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private boolean success;
    private String sCode;
    private String sMsg;

    public static ErrorResponse of(ResStatus resStatus) {
        return ErrorResponse.builder()
                            .success(false)
                            .sCode(resStatus.getSCode())
                            .sMsg(resStatus.getSMsg())
                            .build();
    }
}