package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangingLanguageRequestDto {
    private String displayLanguage;
    private String region;
    private String timeZone;
}
