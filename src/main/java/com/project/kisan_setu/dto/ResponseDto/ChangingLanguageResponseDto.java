package com.project.kisan_setu.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangingLanguageResponseDto {
    private Long changingLanguageId;
    private String displayLanguage;
    private String region;
    private String timeZone;
}
