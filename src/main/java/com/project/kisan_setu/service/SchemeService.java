package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.SchemeRequestDto;
import com.project.kisan_setu.dto.ResponseDto.SchemeResponseDto;

import java.util.List;

public interface SchemeService {
     SchemeResponseDto createScheme(SchemeRequestDto dto);
     SchemeResponseDto getSchemeById(Long schemeId);

     List<SchemeResponseDto> getAllSchemes();

    public SchemeResponseDto updateSchemeById(Long schemeId, SchemeRequestDto dto);

    public void deleteScheme(Long id);
}
