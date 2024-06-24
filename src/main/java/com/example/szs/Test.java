package com.example.szs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Test {
    public static void main(String[] args)  {
        TaxCalculator taxCalculator = new TaxCalculator(new BigDecimal("20000000"), new BigDecimal("2900000"), new BigDecimal("300000"));
        taxCalculator.calculatedTax();
    }
}
