package com.example.szs.exception;

import com.example.szs.model.eNum.ResStatus;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

public class CustomException extends Exception {
    private ResStatus resStatus;

    public CustomException(ResStatus resStatus) {
        super(resStatus.toString());
        this.resStatus = resStatus;
    }

    public ResStatus getResStatus() {
        return resStatus;
    }
}
