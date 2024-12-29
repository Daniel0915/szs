package com.example.szs.utils.jpa;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityToDtoMapper {
    public static <E, D> Optional<D> mapEntityToDto(E entity, Class<D> dtoClass) {
        if (entity == null) {
            return Optional.empty();
        }

        try {
            // DTO 객체 생성
            D dto = dtoClass.getDeclaredConstructor().newInstance();

            // Entity와 DTO 필드 매칭
            Map<String, Field> entityFields = getFieldMap(entity.getClass());
            Map<String, Field> dtoFields = getFieldMap(dtoClass);

            for (String fieldName : dtoFields.keySet()) {
                Field entityField = entityFields.get(fieldName);
                Field dtoField = dtoFields.get(fieldName);

                if (entityField != null && dtoField != null) {
                    entityField.setAccessible(true);
                    dtoField.setAccessible(true);

                    // 필드 값 복사
                    Object value = entityField.get(entity);
                    dtoField.set(dto, value);
                }
            }

            return Optional.of(dto);

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static Map<String, Field> getFieldMap(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                fieldMap.put(field.getName(), field);
            }
            clazz = clazz.getSuperclass(); // 상위 클래스 필드 포함
        }
        return fieldMap;
    }
}
