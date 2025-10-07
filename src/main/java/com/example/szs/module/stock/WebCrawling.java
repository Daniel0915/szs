package com.example.szs.module.stock;


import com.example.szs.insideTrade.application.dto.ExecOwnershipDetailDTO;
import com.example.szs.model.dto.largeHoldings.LargeHoldingsDetailDTO;
import com.example.szs.insideTrade.presentation.dto.response.LargeHoldingsStkrtResDTO;
import com.example.szs.utils.money.NumberUtils;
import com.example.szs.utils.time.TimeUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebCrawling {
    private final String detailUrl = "https://dart.fss.or.kr/dsaf001/main.do?rcpNo=";

    public List<LargeHoldingsDetailDTO> getLargeHoldingsDetailCrawling(String rceptNo, String corpCode, String corpName) {
        assert ( StringUtils.hasText(rceptNo) && StringUtils.hasText(corpCode) && StringUtils.hasText(corpName) ) : "rceptNo, corpCode, corpName must be have value";

        List<LargeHoldingsDetailDTO> result = new ArrayList<>();
        WebDriver driver = this.getSettingChromeDriver();
        try {

            driver.get(detailUrl + rceptNo);
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
            log.error("=============error [start]==============");
            log.error("rceptNo : {}, corpCode: {}, corpName: {}", rceptNo, corpCode, corpName);
            log.error(e.getMessage());
            log.error("=============error [end]==============");
        } finally {
            driver.quit();
        }
        return result.stream()
                     .sorted(Comparator.comparing(LargeHoldingsDetailDTO::getTradeDt))
                     .collect(Collectors.toList());
    }

    public List<LargeHoldingsStkrtResDTO> getLargeHoldingsStkrtCrawling(String rceptNo, String corpCode, String corpName) {
        assert ( StringUtils.hasText(rceptNo) && StringUtils.hasText(corpCode) && StringUtils.hasText(corpName) ) : "rceptNo, corpCode, corpName must be have value";

        WebDriver driver = this.getSettingChromeDriver();

        List<LargeHoldingsStkrtResDTO> result = new ArrayList<>();
        try {
            driver.get(detailUrl + rceptNo);
            // 페이지 로딩을 위한 대기 (명시적 대기)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement msgElement = driver.findElement(By.id("winCommMsg"));
            if (msgElement.isEnabled()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none';", msgElement);
            }

            // 보고자 및 특별관계자
            WebElement targetTab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), '보고자 및 특별관계자')]")));
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

            // 테이블의 각 행에서 데이터 추출
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

                // trim
                rceptNo = rceptNo.trim();
                corpCode= corpCode.trim();
                corpName= corpName.trim();
                largeHoldingsName = largeHoldingsName.trim();
                birthDateOrBizRegNum = birthDateOrBizRegNum.trim();

                if (!StringUtils.hasText(largeHoldingsName) || Objects.equals(largeHoldingsName, "-")) {
                    continue;
                }

                result.add(LargeHoldingsStkrtResDTO.builder()
                                                   .rceptNo(rceptNo)
                                                   .corpCode(corpCode)
                                                   .corpName(corpName)
                                                   .largeHoldingsName(largeHoldingsName)
                                                   .birthDateOrBizRegNum(birthDateOrBizRegNum)
                                                   .totalStockAmount(totalStockAmount)
                                                   .stkrt(stkrt)
                                                   .build());
            }
        } catch (Exception e) {
            log.error("=============error [start]==============");
            log.error("rceptNo : {}, corpCode: {}, corpName: {}", rceptNo, corpCode, corpName);
            log.error(e.getMessage());
            log.error("=============error [end]==============");
        } finally {
            driver.quit();
        }
        return result;
    }

    public List<ExecOwnershipDetailDTO> getExecOwnershipDetailCrawling(String rceptNo,
                                                                       String corpCode,
                                                                       String corpName,
                                                                       String execOwnershipName,
                                                                       String isuExctvRgistAt,
                                                                       String isuExctvOfcps,
                                                                       String isuMainShrholdr) {
        assert ( StringUtils.hasText(rceptNo) && StringUtils.hasText(corpCode) && StringUtils.hasText(corpName) ) : "rceptNo, corpCode, corpName must be have value";

        Random random = new Random();

        List<ExecOwnershipDetailDTO> result = new ArrayList<>();
        WebDriver driver = this.getSettingChromeDriver();
        try {

            driver.get(detailUrl + rceptNo);
            Thread.sleep(3000 + random.nextInt(3000)); // 3~6초 랜덤 대기
            // 페이지 로딩을 위한 대기 (명시적 대기)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement msgElement = driver.findElement(By.id("winCommMsg"));
            if (msgElement.isEnabled()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none';", msgElement);
            }

            // 해당 XPath 요소가 클릭 가능할 때까지 대기
            WebElement targetTab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), '특정증권등의')]")));

            // 클릭 실행
            targetTab.click();

            // iframe 의 src 속성 값 가져오기
            // iframe 요소가 로드될 때까지 대기
            WebElement iframeElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ifrm")));

            // iframe 의 src 속성 값 가져오기
            String iframeSrc = iframeElement.getAttribute("src");

            Thread.sleep(3000 + random.nextInt(3000)); // 3~6초 랜덤 대기
            driver.get(iframeSrc);
            // /html/body/table[4]

            WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/table[4]")));

            // 테이블에서 모든 행(row) 추출
            List<WebElement> rows = table.findElements(By.tagName("tr"));

            // 테이블의 각 행에서 데이터 추출
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                if (rowIndex == (rows.size() - 1)) {
                    continue;
                }

                // 각 행에서 모든 셀(td) 추출
                List<WebElement> cells = rows.get(rowIndex).findElements(By.tagName("td"));
                if (CollectionUtils.isEmpty(cells)) {
                    continue;
                }

                // 각 셀 데이터 출력
                String tradeReason = "";
                String tradeDt = "";
                String stockType = "";
                Long beforeStockAmount = 0L;
                Long changeStockAmount = 0L;
                Long afterStockAmount = 0L;
                String unitStockPrice = "";

                for (int i = 0; i < cells.size(); i++) {
                    String value = cells.get(i).getText() == null ? "" : cells.get(i).getText().trim();

                    switch (i) {
                        case 0:
                            tradeReason = value;
                            break;
                        case 1:
                            tradeDt = TimeUtil.korDateToTime(value);
                            break;
                        case 2:
                            stockType = value;
                            break;
                        case 3:
                            beforeStockAmount = NumberUtils.stringToLongConverter(value); // 숫자 형 변환 예시
                            break;
                        case 4:
                            changeStockAmount = NumberUtils.stringToLongConverter(value);
                            break;
                        case 5:
                            afterStockAmount = NumberUtils.stringToLongConverter(value);
                            break;
                        case 6:
                            unitStockPrice = value;
                            break;
                    }
                }

                result.add(ExecOwnershipDetailDTO.builder()
                                                 .rceptNo(rceptNo)
                                                 .corpCode(corpCode)
                                                 .corpName(corpName)
                                                 .execOwnershipName(execOwnershipName)
                                                 .isuExctvRgistAt(isuExctvRgistAt)
                                                 .isuExctvOfcps(isuExctvOfcps)
                                                 .isuMainShrholdr(isuMainShrholdr)
                                                 .tradeDt(tradeDt)
                                                 .tradeReason(tradeReason)
                                                 .stockType(stockType)
                                                 .beforeStockAmount(beforeStockAmount)
                                                 .changeStockAmount(changeStockAmount)
                                                 .afterStockAmount(afterStockAmount)
                                                 .unitStockPrice(unitStockPrice)
                                                 .build());

            }
        } catch (Exception e) {
            log.error("=============error [start]==============");
            log.error("rceptNo : {}, corpCode: {}, corpName: {}", rceptNo, corpCode, corpName);
            log.error(e.getMessage());
            log.error("=============error [end]==============");
        } finally {
            driver.quit();
        }
        return result.stream()
                     .sorted(Comparator.comparing(ExecOwnershipDetailDTO::getTradeDt))
                     .collect(Collectors.toList());
    }

    private WebDriver getSettingChromeDriver() {
        // ChromeDriver 자동 설치
        WebDriverManager.chromedriver().setup();

        // Chrome 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Headless 모드 활성화
        options.addArguments("--disable-gpu"); // GPU 비활성화 (Linux에서 필요할 수 있음)
        options.addArguments("--window-size=1920,1080"); // 가상 해상도 설정
        options.addArguments("--no-sandbox"); // 리눅스 환경에서 필요
        options.addArguments("--disable-dev-shm-usage"); // 공유 메모리 이슈 방지

        // WebDriver 생성
        return new ChromeDriver(options);
    }
}
