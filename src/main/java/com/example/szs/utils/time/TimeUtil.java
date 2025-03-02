package com.example.szs.utils.time;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class TimeUtil {
    public static String nowTime(String pattern) {
        LocalDateTime now = LocalDateTime.now();
        // 원하는 포맷 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        // 포맷 적용하여 문자열로 변환
        return now.format(formatter);
    }

    public static String korDateToTime(String korDate) {
        if (Objects.equals("-", korDate)) {
            return "";
        }

        // 가능한 날짜 형식 정의
        SimpleDateFormat[] originalFormats = {
                new SimpleDateFormat("yyyy년 MM월 dd일"),
                new SimpleDateFormat("yyyy.MM.dd")
        };
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyyMMdd");

        for (SimpleDateFormat originalFormat : originalFormats) {
            try {
                // 날짜 문자열을 Date 객체로 변환
                Date date = originalFormat.parse(korDate);
                return targetFormat.format(date);
            } catch (ParseException ignored) {
                // 파싱 실패 시 다음 형식으로 넘어감
            }
        }

        // 모든 형식에서 변환 실패 시
        return korDate;
    }
}
