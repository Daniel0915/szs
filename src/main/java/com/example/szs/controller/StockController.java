package com.example.szs.controller;

import com.example.szs.model.dto.LHResponseDTO;
import com.example.szs.service.stock.LargeHoldingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {
    private final LargeHoldingsService largeHoldingsService;

    @GetMapping("/test")
    public LHResponseDTO test() {
//        largeHoldingsService.insertData();
        return largeHoldingsService.insertData();
    }
}
