package com.example.szs.insideTrade.domain;

import java.util.List;

public interface LargeHoldingsStkrtRepo {
    List<LargeHoldingsStkrt> saveAll(List<LargeHoldingsStkrt> entities);
}