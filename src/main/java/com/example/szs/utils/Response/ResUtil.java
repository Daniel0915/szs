package com.example.szs.utils.Response;

import com.example.szs.exception.CustomException;
import com.example.szs.model.eNum.ResStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResUtil {
    public static <T> Map<String, Object> makeResponse(T t, ResStatus resStatus) {
        Map<String, Object> result = new HashMap();
        result.put("sCode", resStatus.getSCode());
        result.put("sMsg", resStatus.getSCode());
        result.put("data", t);
        return result;
    }

    public static <T> Map<String, Object> makeResponse(T t, String resStatuCode) {
        ResStatus resStatus = (ResStatus) Arrays.stream(ResStatus.values()).filter((status) -> {
            return status.getSCode().equals(resStatuCode);
        }).findFirst().orElseGet(() -> {
            return ResStatus.ERROR;
        });
        Map<String, Object> result = new HashMap();
        result.put("sCode", resStatus.getSCode());
        result.put("sMsg", resStatus.getSMsg());
        result.put("data", t);
        return result;
    }
//    public static <T> Map<String, Object> makeErrorResponse(Exception e) {
//        e.printStackTrace();
//        if (e instanceof CustomException) {
//            CustomException customException = (CustomException) e;
//            return ResUtil.makeResponse("", customException.getResStatus());
//        }
//
//        if (e instanceof BadCredentialsException) {
//            return ResUtil.makeResponse("", ResStatus.LOGIN_ERROR);
//        }
//
//        return ResUtil.makeResponse("", ResStatus.ERROR);
//    }

    public static void makeForbiddenResponse(HttpServletResponse response, ResStatus resStatus) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(ResUtil.makeResponse("", resStatus));
        response.getWriter().print(result);
    }
}
