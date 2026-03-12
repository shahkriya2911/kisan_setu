package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.ChangingLanguageRequestDto;
import com.project.kisan_setu.dto.ResponseDto.ChangingLanguageResponseDto;

public interface ChangingLanguageService {
    ChangingLanguageResponseDto getMyChangingLanguage();
    ChangingLanguageResponseDto postChangingLanguage(ChangingLanguageRequestDto changingLanguageRequestDto);

    ChangingLanguageResponseDto updateChangingLanguage(Long changingLanguageId,ChangingLanguageRequestDto changingLanguageRequestDto);
}
