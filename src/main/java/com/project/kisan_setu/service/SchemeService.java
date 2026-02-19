package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.SchemeRequestDto;
import com.project.kisan_setu.dto.SchemeResponseDto;
import com.project.kisan_setu.entity.Scheme;

import java.util.List;

public interface SchemeService {
     SchemeResponseDto createScheme(SchemeRequestDto dto);
     SchemeResponseDto getSchemeById(Long schemeId);

     List<SchemeResponseDto> getAllSchemes();

    public SchemeResponseDto updateSchemeById(Long schemeId, SchemeRequestDto dto);

    public void deleteScheme(Long id);
}
