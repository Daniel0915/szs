package com.example.szs.model.dto;

import com.example.szs.config.json.NullToEmptySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonSerialize(using = NullToEmptySerializer.class)
public class MarketingMngDetailDTO {
    private int page;
    private String img;
    private String question;
    private String answer1;
    private String answer1Score;
    private String answer2;
    private String answer2Score;
}
