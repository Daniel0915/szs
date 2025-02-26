package com.example.szs.model.dto.corpInfo;

import com.example.szs.config.json.NullToEmptySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = NullToEmptySerializer.class)
@FieldNameConstants
public class CorpInfoDTO {
    private String corpCode;
    private String corpName;
}
