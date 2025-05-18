package com.example.szs.utils.Response;

import com.example.szs.model.eNum.ResStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String sCode;
    private String sMsg;

    public static <T> ApiResponse<T> success(T data, ResStatus resStatus) {
        return ApiResponse.<T>builder()
                          .success(true)
                          .data(data)
                          .sCode(resStatus.getSCode())
                          .sMsg(resStatus.getSMsg())
                          .build();
    }
}