package com.example.szs.insideTrade.infrastructure.client;

import com.example.szs.insideTrade.domain.CorpInfo;
import com.example.szs.insideTrade.infrastructure.client.dto.ExecOwnershipDetailCrawlingDTO;
import com.example.szs.insideTrade.infrastructure.client.dto.ExecOwnershipInsiderTradeApiRes;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsDetailCrawlingDTO;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsInsiderTradeApiRes;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsStkrtCrawlingDTO;
import com.example.szs.common.utils.money.NumberUtils;
import com.example.szs.common.utils.time.TimeUtil;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class Dart {
    @Value("${dart.uri.base}")
    private String baseUri;
    @Value("${dart.uri.largeHoldings}")
    private String pathLargeHoldings;
    @Value("${dart.uri.execOwnership}")
    private String pathExecOwnership;
    @Value("${corp.code.key}")
    private String corpCodeKey;
    @Value("${dart.key}")
    private String dartKey;
    @Value("${dart.value}")
    private String dartValue;
    private static final String detailUrl = "https://dart.fss.or.kr/dsaf001/main.do?rcpNo=";

    public Optional<LargeHoldingsInsiderTradeApiRes> findLargeHoldingsInsiderTrade(CorpInfo corpInfo) {
        WebClient webClient = WebClient.builder()
                                       .baseUrl(baseUri)
                                       .build();
        // TODO : 왜 Mono 를 사용했는지 작성해보자~
        Mono<LargeHoldingsInsiderTradeApiRes> resMono = webClient.get()
                                                                 .uri(uriBuilder -> uriBuilder.path(pathLargeHoldings)
                                                                                              .queryParam(dartKey, dartValue)
                                                                                              .queryParam(corpCodeKey, corpInfo.getCorpCode())
                                                                                              .build())
                                                                 .retrieve()
                                                                 .bodyToMono(LargeHoldingsInsiderTradeApiRes.class);
        return resMono.blockOptional();
    }

    public Optional<ExecOwnershipInsiderTradeApiRes> findExecOwnershipInsiderTrade(CorpInfo corpInfo) {
        WebClient webClient = WebClient.builder()
                                       .baseUrl(baseUri)
                                       .build();
        // TODO : 왜 Mono 를 사용했는지 작성해보자~
        Mono<ExecOwnershipInsiderTradeApiRes> resMono = webClient.get()
                                                                 .uri(uriBuilder -> uriBuilder.path(pathExecOwnership)
                                                                                              .queryParam(dartKey, dartValue)
                                                                                              .queryParam(corpCodeKey, corpInfo.getCorpCode())
                                                                                              .build())
                                                                 .retrieve()
                                                                 .bodyToMono(ExecOwnershipInsiderTradeApiRes.class);
        return resMono.blockOptional();
    }

    public List<LargeHoldingsDetailCrawlingDTO> getLargeHoldingsDetailCrawling(String rceptNo, String corpCode, String corpName) {
        assert ( StringUtils.hasText(rceptNo) && StringUtils.hasText(corpCode) && StringUtils.hasText(corpName) ) : "rceptNo, corpCode, corpName must be have value";

        List<LargeHoldingsDetailCrawlingDTO> result = new ArrayList<>();
        WebDriver                            driver = this.getSettingChromeDriver();
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

                result.add(LargeHoldingsDetailCrawlingDTO.builder()
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
                     .sorted(Comparator.comparing(LargeHoldingsDetailCrawlingDTO::getTradeDt))
                     .collect(Collectors.toList());
    }

    public List<LargeHoldingsStkrtCrawlingDTO> getLargeHoldingsStkrtCrawling(String rceptNo, String corpCode, String corpName) {
        assert ( StringUtils.hasText(rceptNo) && StringUtils.hasText(corpCode) && StringUtils.hasText(corpName) ) : "rceptNo, corpCode, corpName must be have value";

        WebDriver driver = this.getSettingChromeDriver();

        List<LargeHoldingsStkrtCrawlingDTO> result = new ArrayList<>();
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

                result.add(LargeHoldingsStkrtCrawlingDTO.builder()
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

    public List<ExecOwnershipDetailCrawlingDTO> getExecOwnershipDetailCrawling(String rceptNo,
                                                                               String corpCode,
                                                                               String corpName,
                                                                               String execOwnershipName,
                                                                               String isuExctvRgistAt,
                                                                               String isuExctvOfcps,
                                                                               String isuMainShrholdr) {
        assert ( StringUtils.hasText(rceptNo) && StringUtils.hasText(corpCode) && StringUtils.hasText(corpName) ) : "rceptNo, corpCode, corpName must be have value";

        Random random = new Random();

        List<ExecOwnershipDetailCrawlingDTO> result = new ArrayList<>();
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

                result.add(ExecOwnershipDetailCrawlingDTO.builder()
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
                     .sorted(Comparator.comparing(ExecOwnershipDetailCrawlingDTO::getTradeDt))
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
