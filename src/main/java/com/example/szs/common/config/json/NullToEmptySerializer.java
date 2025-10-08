package com.example.szs.common.config.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

public class NullToEmptySerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            return;
        }

        Field[] fields = value.getClass().getDeclaredFields();
        gen.writeStartObject();

        boolean isTrySuccess = false;

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldValue = field.get(value);

                if ( Objects.equals(fieldValue, null) || Objects.equals(fieldValue, "null") || Objects.equals(fieldValue, "undefined") ) {
                    setDefaultValue(gen, field);
                } else {
                    gen.writeObjectField(field.getName(), fieldValue);
                }
            }
            isTrySuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!isTrySuccess) {
                originGenChg(value, gen, fields);
            }
        }
        gen.writeEndObject();
    }

    private void setDefaultValue(JsonGenerator gen, Field field) throws IOException, IllegalAccessException {
        Class<?> fieldType = field.getType();

        if (fieldType == Integer.class) {
            gen.writeObjectField(field.getName(), 0);
        } else if (fieldType == Long.class) {
            gen.writeObjectField(field.getName(), 0L);
        } else if (fieldType == Float.class) {
            gen.writeObjectField(field.getName(), 0.0f);
        } else if (fieldType == Double.class) {
            gen.writeObjectField(field.getName(), 0.0d);
        } else if (fieldType == Short.class) {
            gen.writeObjectField(field.getName(), (short) 0);
        } else if (fieldType == Byte.class) {
            gen.writeNumber((byte) 0);
            gen.writeObjectField(field.getName(), (byte) 0);
        } else if (fieldType == String.class) {
            gen.writeObjectField(field.getName(), "");
        }
    }

    private void originGenChg(Object value, JsonGenerator gen, Field[] fields) {
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                gen.writeObjectField(field.getName(), field.get(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
