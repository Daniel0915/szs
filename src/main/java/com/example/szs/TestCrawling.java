package com.example.szs;

import com.example.szs.model.dto.execOwnership.ExecOwnershipDetailDTO;
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
        // 00126371	삼성전기
        List<ExecOwnershipDetailDTO> list = webCrawling.getExecOwnershipDetailCrawling("20250107000296", "00126371", "삼성전기", "", "", "","");

        System.out.println(list.size());

        for(ExecOwnershipDetailDTO dto : list) {
            System.out.println(dto);
        }


    }
}
