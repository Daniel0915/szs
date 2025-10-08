package com.example.szs.common.utils.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ListDivider {

    public static <T> List<List<T>> getDivisionList(List<T> list, int maxCnt){
        AtomicInteger atomic = new AtomicInteger(0);
        return new ArrayList<>(list.stream().collect(Collectors.groupingBy(it -> atomic.getAndIncrement() / maxCnt)).values());
    }
}
