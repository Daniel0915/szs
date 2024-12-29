package com.example.szs;


import com.example.szs.domain.stock.QLargeHoldingsEntity;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

import java.lang.reflect.Field;
import java.util.Objects;

import static com.example.szs.domain.stock.QLargeHoldingsEntity.largeHoldingsEntity;

public class Test {

    public static void main(String[] args) {
        String name = "rceptNo";
        boolean isDescending = true;
        // QLargeHoldingsEntity의 클래스 정보 가져오기
        Class<?> clazz = QLargeHoldingsEntity.class;

        // PathBuilder를 사용하여 필드 동적 참조
        PathBuilder<?> pathBuilder = new PathBuilder<>(largeHoldingsEntity.getType(), largeHoldingsEntity.getMetadata());

        StringPath path = pathBuilder.getString(name);

        OrderSpecifier<?> orderSpecifier = isDescending ? path.desc() : path.asc();


        System.out.println("pathBuilder = " + pathBuilder);

        // 클래스의 모든 필드 정보 출력
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Objects.equals(name, field.getName())) {
                // return
                break;
            }
        }
    }
}
