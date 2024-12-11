package com.example.szs.model.dto;

import com.example.szs.config.json.NullToEmptySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonSerialize(using = NullToEmptySerializer.class)
public class MarketingMngDTO {
    private String title;
    private List<MarketingMngDetailDTO> listContent;
    private String userToken;
}
