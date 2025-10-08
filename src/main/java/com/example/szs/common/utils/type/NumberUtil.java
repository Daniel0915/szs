package com.example.szs.common.utils.type;

public class NumberUtil {
    public static boolean isLongInitOrNull(Long value) {
        return value == null || value == 0L;
    }

    public static boolean isIntegerInitOrNull(Integer value) {
        return value == null || value == 0;
    }

    // Double 타입: 값이 null 이거나 0.0일 때 true
    public static boolean isDoubleInitOrNull(Double value) {
        return value == null || value == 0.0;
    }

    // Float 타입: 값이 null 이거나 0.0f일 때 true
    public static boolean isFloatInitOrNull(Float value) {
        return value == null || value == 0.0f;
    }

    // Short 타입: 값이 null 이거나 0일 때 true
    public static boolean isShortInitOrNull(Short value) {
        return value == null || value == 0;
    }

    // Byte 타입: 값이 null 이거나 0일 때 true
    public static boolean isByteInitOrNull(Byte value) {
        return value == null || value == 0;
    }
}
