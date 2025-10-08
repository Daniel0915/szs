package com.example.szs.insideTrade.presentation.dto.response;

import com.example.szs.common.config.json.NullToEmptySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonSerialize(using = NullToEmptySerializer.class)
public class PageResDTO {
    List<?> content;
    Long totalElements;
    Integer totalPages;
}
