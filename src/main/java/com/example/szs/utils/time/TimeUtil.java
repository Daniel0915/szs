package com.example.szs.utils.time;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtil {
    public static String nowTime(String pattern) {
        LocalDateTime now = LocalDateTime.now();
        // 원하는 포맷 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        // 포맷 적용하여 문자열로 변환
        return now.format(formatter);
    }

    public static String korDateToTime(String korDate) {
        assert (StringUtils.hasText(korDate)) : "not empty";

        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyyMMddHH");
        try {
            // 날짜 문자열을 Date 객체로 변환
            Date date = originalFormat.parse(korDate);
            return targetFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
