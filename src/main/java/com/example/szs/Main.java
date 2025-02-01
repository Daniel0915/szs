package com.example.szs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Main {
    public static void main(String[] args) {
        try {
            // HTML 페이지 로드 (예시: URL에서 로드)
            Document doc = Jsoup.connect("https://dart.fss.or.kr/dsaf001/main.do?rcpNo=20250107000144").get();

            // iframe 태그 찾기
            Element iframe = doc.select("iframe").first();
            if (iframe != null) {
                String iframeSrc = iframe.attr("src");
                System.out.println("Iframe src: " + iframeSrc);
            } else {
                System.out.println("Iframe not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
