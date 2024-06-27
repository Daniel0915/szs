package com.example.szs.utils.money;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class TextChg {
    /**
     * 천 단위 구분 기호 포함
     */
    public static String formatBigDecimal(BigDecimal number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(',');

        DecimalFormat df = new DecimalFormat("#,##0", symbols);

        return df.format(number);
    }
}
