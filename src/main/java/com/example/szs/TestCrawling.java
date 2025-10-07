package com.example.szs;

import com.example.szs.model.dto.execOwnership.ExecOwnershipDetailDTO;
import com.example.szs.module.stock.WebCrawling;

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
