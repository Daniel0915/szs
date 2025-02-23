package com.example.szs;

import com.example.szs.domain.stock.LargeHoldingsDetailEntity;
import com.example.szs.model.dto.largeHoldings.LargeHoldingsDetailDTO;
import com.example.szs.utils.jpa.Param;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class TestJpa {
    public static void main(String[] args) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // Create dummy entity
        LargeHoldingsDetailEntity entity = new LargeHoldingsDetailEntity();


        // Create dummy DTO (Directly copy the fields from the entity for simplicity)
        LargeHoldingsDetailDTO dto = LargeHoldingsDetailDTO.builder()
                                                           .seq(1L)
                                                           .build();

        Optional<LargeHoldingsDetailEntity.LargeHoldingsDetailEntityBuilder> test = Param.getSaveEntityToBuilder(dto, entity, entity.toBuilder());

        System.out.println(test.get().build());


    }

}
