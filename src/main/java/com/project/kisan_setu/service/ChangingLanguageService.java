package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ChangingLanguageRequestDto;
import com.project.kisan_setu.dto.ChangingLanguageResponseDto;

public interface ChangingLanguageService {
    ChangingLanguageResponseDto getMyChangingLanguage();
    ChangingLanguageResponseDto postChangingLanguage(ChangingLanguageRequestDto changingLanguageRequestDto);

    ChangingLanguageResponseDto updateChangingLanguage(Long changingLanguageId,ChangingLanguageRequestDto changingLanguageRequestDto);
}
