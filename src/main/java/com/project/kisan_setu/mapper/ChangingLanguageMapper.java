package com.project.kisan_setu.mapper;
import com.project.kisan_setu.dto.RequestDto.ChangingLanguageRequestDto;
import com.project.kisan_setu.dto.ResponseDto.ChangingLanguageResponseDto;
import com.project.kisan_setu.entity.ChangingLanguage;

public class ChangingLanguageMapper {
    public static ChangingLanguageResponseDto toDto(ChangingLanguage changingLanguage){
        ChangingLanguageResponseDto changingLanguageResponseDto = new ChangingLanguageResponseDto();
        changingLanguageResponseDto.setChangingLanguageId(changingLanguage.getChangingLanguageId());
        changingLanguageResponseDto.setDisplayLanguage(changingLanguage.getDisplayLanguage());
        changingLanguageResponseDto.setRegion(changingLanguage.getRegion());
        changingLanguageResponseDto.setTimeZone(changingLanguage.getTimeZone());

        return changingLanguageResponseDto;
    }

    public static ChangingLanguage toEntity(ChangingLanguageRequestDto changingLanguageRequestDto){
        ChangingLanguage changingLanguage = new ChangingLanguage();
        changingLanguage.setDisplayLanguage(changingLanguageRequestDto.getDisplayLanguage());
        changingLanguage.setRegion(changingLanguageRequestDto.getRegion());
        changingLanguage.setTimeZone(changingLanguageRequestDto.getTimeZone());

        return changingLanguage;
    }

    public static ChangingLanguage updateEntity(ChangingLanguage changingLanguage, ChangingLanguageRequestDto changingLanguageRequestDto) {
        changingLanguage.setDisplayLanguage(changingLanguageRequestDto.getDisplayLanguage());
        changingLanguage.setRegion(changingLanguageRequestDto.getRegion());
        changingLanguage.setTimeZone(changingLanguageRequestDto.getTimeZone());
        return changingLanguage;
    }
}
