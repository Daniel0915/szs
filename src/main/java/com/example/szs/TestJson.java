package com.example.szs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestJson {
    public static void main(String[] args) {
        String targetUrl = "https://dart.fss.or.kr/dsaf001/main.do?rcpNo=20250107000144";
        try {
            String jsonResponse = fetchHtmlResponse(targetUrl);
            System.out.println(extractJavaScriptVariable(jsonResponse, "treeData"));
//            parseAndExtractData(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fetch HTML response
    public static String fetchHtmlResponse(String targetUrl) throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "text/html; charset=UTF-8");

        if (connection.getResponseCode() == 200) {
            return new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } else {
            throw new IOException("Failed to fetch response, HTTP code: " + connection.getResponseCode());
        }
    }

    // Parse JSON and extract target node data
    public static void parseAndExtractData(String jsonResponse) throws IOException {
        // Replace the JSON parsing part as per your input data structure
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        // Assuming `node2` exists in the structure
        JsonNode node2 = rootNode.path("node2");
        if (node2 != null && node2.isObject()) {
            String text = node2.path("text").asText();
            if ("2. 세부변동내역".equals(text)) {
                System.out.println("Found target node: " + text);
                Iterator<String> fieldNames = node2.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    System.out.println(fieldName + ": " + node2.get(fieldName));
                }
            } else {
                System.out.println("Target text not found in the node.");
            }
        } else {
            System.out.println("node2 not found or is not an object.");
        }
    }

    public static String extractJavaScriptVariable(String html, String variableName) {
        // Jsoup을 사용하여 <script> 태그 추출
        Document doc = Jsoup.parse(html);
        for (Element script : doc.select("script")) {
            String scriptContent = script.html();
            String[] lines = scriptContent.split("\n");

            // 2. "text" 필드에 조건을 만족하는 데이터를 추출
            String targetText = "2. 세부변동내역";
            List<String> extractedLines = new ArrayList<>();
            boolean isMatchingNode = false;

            for (String line : lines) {
                line = line.trim(); // 공백 제거
                if (line.contains("text") && line.contains(targetText)) {
                    isMatchingNode = true; // 조건에 맞는 노드 발견
                }
                if (isMatchingNode) {
                    extractedLines.add(line); // 조건을 만족하는 노드 데이터 추가
                    if (line.startsWith("cnt++;")) { // 노드 종료 조건
                        break;
                    }
                }
            }

            // 3. 결과 출력
            if (!extractedLines.isEmpty()) {
                Map<String, String> resultMap = new HashMap<>();

                for (String data : extractedLines) {
                    // 데이터에서 key와 value를 추출
                    String[] parts = data.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim().replace("node2['", "").replace("']", "");
                        String value = parts[1].trim().replace(";", "").replace("\"", "");
                        resultMap.put(key, value);
                    }
                }

                // 결과 출력
                System.out.println("Result Map: " + resultMap);
            }



            // 정규식을 사용하여 변수 내용 추출
//            String regex = String.format("var %s\\s*=\\s*(\\{.*?\\});", variableName); // 정규식 패턴
//            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
//            Matcher matcher = pattern.matcher(scriptContent);
//
//            if (matcher.find()) {
//                return matcher.group(1); // 변수 값 반환
//            }
        }
        return null; // 변수를 찾을 수 없는 경우
    }
}
