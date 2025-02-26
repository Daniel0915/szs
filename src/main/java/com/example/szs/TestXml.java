package com.example.szs;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.example.szs.model.dto.corpInfo.CorpInfoDTO;
import org.w3c.dom.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestXml {
    public static void main(String[] args) {
        try {
            // XML 파일 경로
            File file = new File("/Users/peter/Downloads/CORPCODE.xml");

            // DocumentBuilderFactory와 DocumentBuilder 준비
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // XML 파일 파싱
            Document document = builder.parse(file);

            // 루트 엘리먼트 가져오기
            Element rootElement = document.getDocumentElement();

            // <list> 엘리먼트들을 가져오기
            NodeList listNodes = rootElement.getElementsByTagName("list");

            List<CorpInfoDTO> corpInfoList = new ArrayList<>();

            for (int i = 0; i < listNodes.getLength(); i++) {
                Node listNode = listNodes.item(i);
                if (listNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element listElement = (Element) listNode;

                    // <corp_code>와 <corp_name> 엘리먼트 값 가져오기
                    String corpCode = listElement.getElementsByTagName("corp_code").item(0).getTextContent();
                    String corpName = listElement.getElementsByTagName("corp_name").item(0).getTextContent().trim();

                    corpInfoList.add(new CorpInfoDTO(corpCode, corpName));
                }
            }

            for (CorpInfoDTO corpInfo : corpInfoList) {
                System.out.println(corpInfo.toString());
            }

            System.out.println(corpInfoList.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
