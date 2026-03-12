package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.RequestDto.SchemeRequestDto;
import com.project.kisan_setu.dto.ResponseDto.SchemeResponseDto;
import com.project.kisan_setu.entity.Scheme;

public class SchemeMapper {
    public static SchemeResponseDto toDto(Scheme scheme){
        if (scheme == null) {
            return null;
        }
        return new SchemeResponseDto(
                scheme.getSchemeId(),
                scheme.getSchemeTitle(),
                scheme.getSchemeFullName(),
                scheme.getSchemeCategory(),
                scheme.getSchemeDescription(),
                scheme.getSchemeBenefits(),
                scheme.getSchemeEligibility(),
                scheme.getSchemeState(),
                scheme.getSchemeOfficialLink(),
                scheme.getSchemeLastUpdatedDate()
        );
    }
    public static Scheme toEntity(SchemeRequestDto dto)
    {
        if(dto == null){
        return null;
        }

        Scheme scheme=new Scheme();
        scheme.setSchemeTitle(dto.getSchemeTitle());
        scheme.setSchemeFullName(dto.getSchemeFullName());
        scheme.setSchemeCategory(dto.getSchemeCategory());
        scheme.setSchemeDescription(dto.getSchemeDescription());
        scheme.setSchemeBenefits(dto.getSchemeBenefits());
        scheme.setSchemeEligibility(dto.getSchemeEligibility());
        scheme.setSchemeState(dto.getSchemeState());
        scheme.setSchemeOfficialLink(dto.getSchemeOfficialLink());

        return scheme;
    }

    public static void updateEntity(Scheme scheme, SchemeRequestDto dto) {

        scheme.setSchemeTitle(dto.getSchemeTitle());
        scheme.setSchemeFullName(dto.getSchemeFullName());
        scheme.setSchemeCategory(dto.getSchemeCategory());
        scheme.setSchemeDescription(dto.getSchemeDescription());
        scheme.setSchemeBenefits(dto.getSchemeBenefits());
        scheme.setSchemeEligibility(dto.getSchemeEligibility());
        scheme.setSchemeState(dto.getSchemeState());
        scheme.setSchemeOfficialLink(dto.getSchemeOfficialLink());
    }
}
