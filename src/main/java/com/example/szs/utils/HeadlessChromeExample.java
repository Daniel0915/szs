package com.example.szs.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class HeadlessChromeExample {
    public static void main(String[] args) {
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
        WebDriver driver = new ChromeDriver(options);

        // 테스트 실행
        driver.get("https://www.google.com");
        System.out.println("페이지 제목: " + driver.getTitle());

        // WebDriver 종료
        driver.quit();
    }
}
