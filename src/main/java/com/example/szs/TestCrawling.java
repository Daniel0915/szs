package com.example.szs;

import com.example.szs.model.dto.LargeHoldingsDetailDTO;
import com.example.szs.model.dto.user.LargeHoldingsStkrtDTO;
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
        // WebDriver 자동 설치 및 실행
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            // DART 특정 공시 페이지 접속
            String url = "https://dart.fss.or.kr/dsaf001/main.do?rcpNo=20250214002629";
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement msgElement = driver.findElement(By.id("winCommMsg"));
            if (msgElement.isEnabled()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none';", msgElement);
            }

            // 해당 XPath 요소가 클릭 가능할 때까지 대기
            WebElement targetTab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), '보고자 및 특별관계자별 보유내역')]")));

            // 클릭 실행
            targetTab.click();

            // iframe의 src 속성 값 가져오기
            // iframe 요소가 로드될 때까지 대기
            WebElement iframeElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ifrm")));

            // iframe의 src 속성 값 가져오기
            String iframeSrc = iframeElement.getAttribute("src");

            driver.get(iframeSrc);

            WebDriverWait waitByIframeSrc = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/table[1]/tbody")));

            // 테이블에서 모든 행(row) 추출
            List<WebElement> rows = table.findElements(By.tagName("tr"));

            List<LargeHoldingsStkrtDTO> largeHoldingsStkrtDTOList = new ArrayList<>();
            for (WebElement row : rows) {

                // 각 셀 데이터 출력
                String largeHoldingsName = "";
                String birthDateOrBizRegNum = "";
                Long totalStockAmount = 0L;
                Float stkrt = 0F; // 지분비율

                // 각 행에서 모든 셀(td) 추출
                List<WebElement> cells = row.findElements(By.tagName("td"));

                if (CollectionUtils.isEmpty(cells)) {
                    continue;
                }

                // 보고자 레코드행이면, 데이터를 스크래핑 X
                String firstValue = cells.get(0).getText() == null ? "" : cells.get(0).getText().trim();
                boolean isReportTr = firstValue.contains("보고자");

                // 특별관계자 레코드행이면, 데이터를 다음 index 로 스크래핑한다.
                boolean isSpecialTrInclude = firstValue.contains("특별");

                // 더 이상 스크래핑하지 않는 경우면, 스크래핑 중지
                boolean isEndTr = !StringUtils.hasText(firstValue);

                if (isReportTr) {
                    continue;
                }

                if (isEndTr) {
                    break;
                }

                for (int i = 0; i < cells.size(); i++) {
                    String value = cells.get(i).getText() == null ? "" : cells.get(i).getText().trim();

                    if (isSpecialTrInclude) {
                        switch (i) {
                            case 1:
                                largeHoldingsName = value;
                                break;
                            case 2:
                                birthDateOrBizRegNum = value;
                                break;
                            case 12:
                                totalStockAmount = NumberUtils.stringToLongConverter(value);
                                break;
                            case 13:
                                stkrt = NumberUtils.stringToFloatConverter(value);
                                break;
                        }
                        continue;
                    }

                    switch (i) {
                        case 0:
                            largeHoldingsName = value;
                            break;
                        case 1:
                            birthDateOrBizRegNum = value;
                            break;
                        case 11:
                            totalStockAmount = NumberUtils.stringToLongConverter(value);
                            break;
                        case 12:
                            stkrt = NumberUtils.stringToFloatConverter(value);
                            break;
                    }
                }

                largeHoldingsStkrtDTOList.add(LargeHoldingsStkrtDTO.builder()
                                                                   .largeHoldingsName(largeHoldingsName)
                                                                   .birthDateOrBizRegNum(birthDateOrBizRegNum)
                                                                   .totalStockAmount(totalStockAmount)
                                                                   .stkrt(stkrt)
                                                                   .build());
            }

            for (LargeHoldingsStkrtDTO dto : largeHoldingsStkrtDTOList) {
                System.out.println(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 브라우저 종료
            driver.quit();
        }
    }

    public static void iframeTable(String url, WebDriver driver) {
        try {
            driver.get(url);

            // 명시적 대기 객체 생성
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 테이블이 로드될 때까지 대기
            WebElement table = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/table[2]"))
            );

            // 테이블에서 모든 행(row) 추출
            List<WebElement> rows = table.findElements(By.tagName("tr"));

            List<LargeHoldingsDetailDTO> largeHoldingsDetailDTOList = new ArrayList<>();


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

                largeHoldingsDetailDTOList.add(LargeHoldingsDetailDTO.builder()
                                                                     .largeHoldingsName(largeHoldingsName)
                                                                     .birthDateOrBizRegNum(birthDateOrBizRegNum)
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
            for (LargeHoldingsDetailDTO dto : largeHoldingsDetailDTOList) {
                System.out.println(dto.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 브라우저 종료
            driver.quit();
        }
    }
}
