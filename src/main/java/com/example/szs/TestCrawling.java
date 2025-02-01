package com.example.szs;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class TestCrawling {
    public static void main(String[] args) {
        // WebDriver 자동 설치 및 실행
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            // DART 특정 공시 페이지 접속
            String url = "https://dart.fss.or.kr/dsaf001/main.do?rcpNo=20250107000144";
            driver.get(url);

            // 페이지 로딩을 위한 대기 (명시적 대기)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 해당 XPath 요소가 클릭 가능할 때까지 대기
            WebElement targetTab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='17_anchor']")));

            // 클릭 실행
            targetTab.click();

            // iframe의 src 속성 값 가져오기
            // iframe 요소가 로드될 때까지 대기
            WebElement iframeElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ifrm")));

            // iframe의 src 속성 값 가져오기
            String iframeSrc = iframeElement.getAttribute("src");

            iframeTable(iframeSrc, driver);

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

            // 테이블의 각 행에서 데이터 추출
            for (WebElement row : rows) {
                // 각 행에서 모든 셀(td) 추출
                List<WebElement> cells = row.findElements(By.tagName("td"));

                // 각 셀 데이터 출력
                for (WebElement cell : cells) {
                    System.out.print(cell.getText() + " | ");  // 셀 값 출력
                }
                System.out.println();  // 각 행의 끝에서 줄바꿈
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 브라우저 종료
            driver.quit();
        }
    }
}
