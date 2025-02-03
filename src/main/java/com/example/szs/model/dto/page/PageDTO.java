package com.example.szs.model.dto.page;

import com.example.szs.config.json.NullToEmptySerializer;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonSerialize(using = NullToEmptySerializer.class)
public class PageDTO {
    List<?> content;
    Long totalElements;
    Integer totalPages;
}
