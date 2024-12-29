package com.example.szs.utils.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static String nowTime(String pattern) {
        LocalDateTime now = LocalDateTime.now();
        // 원하는 포맷 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        // 포맷 적용하여 문자열로 변환
        return now.format(formatter);
    }
}
