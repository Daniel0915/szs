package com.example.szs;

import com.example.szs.utils.jpa.ListDivider;

import java.util.Arrays;
import java.util.List;

public class TestJpa {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("1111", "1221212", "1111");

        for (List<String> el : ListDivider.getDivisionList(list, 2)) {
            System.out.println(el);

        }


    }

}
