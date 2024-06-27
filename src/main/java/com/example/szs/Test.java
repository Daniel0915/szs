package com.example.szs;

import com.example.szs.utils.TaxCalculator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Test {
    public static void main(String[] args)  {
        TaxCalculator taxCalculator = new TaxCalculator(new BigDecimal("20000000"), new BigDecimal("2900000"), new BigDecimal("300000"));
        taxCalculator.calculatedTax();

        BigDecimal number = new BigDecimal("100500000000000000000000");

        // DecimalFormat을 사용하여 원하는 포맷으로 변환
        String formatted = formatBigDecimal(number);
        System.out.println("Formatted: " + formatted);
    }

    public static String formatBigDecimal(BigDecimal number) {
        // DecimalFormat 생성 (원하는 포맷과 로케일 설정)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(',');

        DecimalFormat df = new DecimalFormat("#,##0", symbols);

        // BigDecimal을 문자열로 포맷팅
        return df.format(number);
    }
}
