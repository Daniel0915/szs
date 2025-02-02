package com.example.szs.utils.jpa;

import com.example.szs.utils.type.NumberUtil;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;

public class Param {
    public static <D, E, BE> Optional<BE> getSaveEntityToBuilder(D dto, E entity, BE entityTobuilder) {
        Class<?> dtoClass = dto.getClass();
        Class<?> entityClass = entity.getClass();

        try {
            Field[] dtoFields = dtoClass.getDeclaredFields();

            E updatedEntity = (E) entityClass.getMethod("toBuilder").invoke(entity);

            for (Field dtoField : dtoFields) {
                dtoField.setAccessible(true);

                Field entityField = entityClass.getDeclaredField(dtoField.getName());
                entityField.setAccessible(true);

                Object dtoValue     = dtoField.get(dto);
                Object entityValue  = entityField.get(entity);

                switch (entityField.getType().getSimpleName()) {
                    case "LONG":
                    case "long":
                        if ( !NumberUtil.isLongInitOrNull((Long) dtoValue) && !Objects.equals(dtoValue, entityValue) ) {
                            updatedEntity.getClass().getMethod(dtoField.getName(), dtoField.getType()).invoke(updatedEntity, dtoValue);
                        }
                        break;
                    case "Integer":
                    case "int":
                        if (!NumberUtil.isIntegerInitOrNull((Integer) dtoValue) && !Objects.equals(dtoValue, entityValue)) {
                            updatedEntity.getClass().getMethod(dtoField.getName(), dtoField.getType()).invoke(updatedEntity, dtoValue);
                        }
                        break;

                    case "Double":
                    case "double":
                        if (!NumberUtil.isDoubleInitOrNull((Double) dtoValue) && !Objects.equals(dtoValue, entityValue)) {
                            updatedEntity.getClass().getMethod(dtoField.getName(), dtoField.getType()).invoke(updatedEntity, dtoValue);
                        }
                        break;

                    case "Float":
                    case "float":
                        if (!NumberUtil.isFloatInitOrNull((Float) dtoValue) && !Objects.equals(dtoValue, entityValue)) {
                            updatedEntity.getClass().getMethod(dtoField.getName(), dtoField.getType()).invoke(updatedEntity, dtoValue);
                        }
                        break;

                    case "Short":
                    case "short":
                        if (!NumberUtil.isShortInitOrNull((Short) dtoValue) && !Objects.equals(dtoValue, entityValue)) {
                            updatedEntity.getClass().getMethod(dtoField.getName(), dtoField.getType()).invoke(updatedEntity, dtoValue);
                        }
                        break;

                    case "Byte":
                    case "byte":
                        if (!NumberUtil.isByteInitOrNull((Byte) dtoValue) && !Objects.equals(dtoValue, entityValue)) {
                            updatedEntity.getClass().getMethod(dtoField.getName(), dtoField.getType()).invoke(updatedEntity, dtoValue);
                        }
                        break;
                    case "String":
                        if ( StringUtils.hasText((String) dtoValue) && !Objects.equals(dtoValue, entityValue) ) {
                            updatedEntity.getClass().getMethod(dtoField.getName(), dtoField.getType()).invoke(updatedEntity, dtoValue);
                        }
                        break;
                    default:
                        updatedEntity.getClass().getMethod(dtoField.getName(), dtoField.getType()).invoke(updatedEntity, dtoValue);
                        break;
                }
            }
            return (Optional<BE>) Optional.of(updatedEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
