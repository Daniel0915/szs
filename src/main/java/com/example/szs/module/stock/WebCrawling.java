package com.example.szs.module.stock;


import com.example.szs.model.dto.LargeHoldingsDetailDTO;
import com.example.szs.utils.money.NumberUtils;
import com.example.szs.utils.time.TimeUtil;
import com.example.szs.utils.type.NumberUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebCrawling {
    private final String largeHoldingsDetailUrl = "https://dart.fss.or.kr/dsaf001/main.do?rcpNo=";

    public List<LargeHoldingsDetailDTO> getLargeHoldingsDetailCrawling(String rceptNo, Long corpCode, String corpName) {
        assert ( StringUtils.hasText(rceptNo) && !NumberUtil.isLongInitOrNull(corpCode) && StringUtils.hasText(corpName) ) : "rceptNo, corpCode, corpName must be have value";

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        List<LargeHoldingsDetailDTO> result = new ArrayList<>();

        try {
            driver.get(largeHoldingsDetailUrl + rceptNo);
            // 페이지 로딩을 위한 대기 (명시적 대기)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement msgElement = driver.findElement(By.id("winCommMsg"));
            if (msgElement.isEnabled()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none';", msgElement);
            }

            // 해당 XPath 요소가 클릭 가능할 때까지 대기
            WebElement targetTab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), '세부변동내역')]")));

            // 클릭 실행
            targetTab.click();

            // iframe의 src 속성 값 가져오기
            // iframe 요소가 로드될 때까지 대기
            WebElement iframeElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ifrm")));

            // iframe의 src 속성 값 가져오기
            String iframeSrc = iframeElement.getAttribute("src");

            driver.get(iframeSrc);

            WebDriverWait waitByIframeSrc = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/table[2]")));

            // 테이블에서 모든 행(row) 추출
            List<WebElement> rows = table.findElements(By.tagName("tr"));

            // 테이블의 각 행에서 데이터 추출
            for (WebElement row : rows) {
                // 각 행에서 모든 셀(td) 추출
                List<WebElement> cells = row.findElements(By.tagName("td"));

                if (CollectionUtils.isEmpty(cells)) {
                    continue;
                }

                // 각 셀 데이터 출력
                String largeHoldingsName = "";
                String birthDateOrBizRegNum = "";
                String tradeDt = "";
                String tradeReason = "";
                String stockType = "";
                Long beforeStockAmount = 0L;
                Long changeStockAmount = 0L;
                Long afterStockAmount = 0L;
                Long unitStockPrice = 0L;
                Long totalStockPrice = 0L;
                String currencyType = "";

                for (int i = 0; i < cells.size(); i++) {
                    String value = cells.get(i).getText() == null ? "" : cells.get(i).getText().trim();

                    switch (i) {
                        case 0:
                            largeHoldingsName = value;
                            break;
                        case 1:
                            birthDateOrBizRegNum = value;
                            break;
                        case 2:
                            tradeDt = TimeUtil.korDateToTime(value);
                            break;
                        case 3:
                            tradeReason = value;
                            break;
                        case 4:
                            stockType = value;
                            break;
                        case 5:
                            beforeStockAmount = NumberUtils.stringToLongConverter(value); // 숫자 형 변환 예시
                            break;
                        case 6:
                            changeStockAmount = NumberUtils.stringToLongConverter(value);
                            break;
                        case 7:
                            afterStockAmount = NumberUtils.stringToLongConverter(value);
                            break;
                        case 8:
                            unitStockPrice = NumberUtils.stringToLongConverter(value);
                            totalStockPrice = unitStockPrice * changeStockAmount;
                            break;
                        case 9:
                            currencyType = value;
                            break;
                    }
                }

                result.add(LargeHoldingsDetailDTO.builder()
                                                 .rceptNo(rceptNo)
                                                 .corpCode(corpCode)
                                                 .corpName(corpName)
                                                 .largeHoldingsName(largeHoldingsName)
                                                 .birthDateOrBizRegNum(birthDateOrBizRegNum)
                                                 .tradeDt(tradeDt)
                                                 .tradeReason(tradeReason)
                                                 .stockType(stockType)
                                                 .beforeStockAmount(beforeStockAmount)
                                                 .changeStockAmount(changeStockAmount)
                                                 .afterStockAmount(afterStockAmount)
                                                 .unitStockPrice(unitStockPrice)
                                                 .currencyType(currencyType)
                                                 .totalStockPrice(totalStockPrice)
                                                 .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return result.stream()
                     .sorted(Comparator.comparing(LargeHoldingsDetailDTO::getTradeDt))
                     .collect(Collectors.toList());
    }
}
