package com.example.szs;

import com.example.szs.model.dto.largeHoldings.LargeHoldingsDetailDTO;
import com.example.szs.model.dto.largeHoldings.LargeHoldingsStkrtDTO;
import com.example.szs.module.stock.WebCrawling;
import com.example.szs.utils.money.NumberUtils;
import com.example.szs.utils.time.TimeUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TestCrawling {
    public static void main(String[] args) {
        WebCrawling webCrawling = new WebCrawling();
        // rceptNo : 20230510000365, corpCode: 00860332, corpName: 메리츠금융지주"
        List<LargeHoldingsStkrtDTO> list = webCrawling.getLargeHoldingsStkrtCrawling("20230510000365", "00860332", "메리츠금융지주");

        for (LargeHoldingsStkrtDTO dto : list) {
            System.out.println(dto);
        }


    }
}
