package com.example.szs.common.utils.money;

import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public class NumberUtils {
    /**
     * 천 단위 구분 기호 포함
     */
    public static String formatBigDecimal(BigDecimal number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(',');

        DecimalFormat df = new DecimalFormat("#,##0", symbols);

        return df.format(number);
    }

    public static Long stringToLongConverter(String str) {
        if (!StringUtils.hasText(str)) {
            return 0L;
        }

        if (Objects.equals("-" , str)) {
            return 0L;
        }

        String valueWithoutCommas = str.replace(",", "");

        return Long.valueOf(valueWithoutCommas);
    }

    public static Float stringToFloatConverter(String str) {
        if (!StringUtils.hasText(str)) {
            return 0F;
        }

        if (Objects.equals(str, "-")) {
            return 0F;
        }

        String valueWithoutCommas = str.replace(",", "");

        return Float.valueOf(valueWithoutCommas);
    }
}
