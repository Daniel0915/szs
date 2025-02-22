package com.example.szs.utils.error;

import com.example.szs.model.eNum.ResStatus;

import java.util.Map;

public class ErrorMsgUtil {
    public static String paramErrorMessage(Map<String, Object> params) {
        StringBuilder errorMessage = new StringBuilder(ResStatus.PARAM_REQUIRE_ERROR.getSMsg()).append(" ");

        if (params == null) {
            return errorMessage.toString().trim();
        }

        int lastIndex = params.size() - 1;
        int index = 0;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            errorMessage.append(entry.getKey())
                        .append(" : ")
                        .append(entry.getValue());

            if (index != lastIndex) {
                errorMessage.append(", ");
            }

            index++;
        }
        return errorMessage.toString().trim();
    }
}
