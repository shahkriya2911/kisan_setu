package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.RequestDto.SchemeRequestDto;
import com.project.kisan_setu.dto.ResponseDto.SchemeResponseDto;
import com.project.kisan_setu.entity.Scheme;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.SchemeMapper;
import com.project.kisan_setu.repository.SchemeRepository;
import com.project.kisan_setu.service.SchemeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchemeServiceImpl implements SchemeService {

    private final SchemeRepository schemeRepository;
    private static final Logger logger = LoggerFactory.getLogger(SchemeServiceImpl.class);

    @Override
    public SchemeResponseDto createScheme(SchemeRequestDto dto) {
        logger.info("Creating new scheme with title: {}", dto.getSchemeTitle());

        Scheme scheme = SchemeMapper.toEntity(dto);
        Scheme savedScheme = schemeRepository.save(scheme);

        logger.info("Scheme created successfully with ID: {}", savedScheme.getSchemeId());

        return SchemeMapper.toDto(savedScheme);
    }

    @Override
    public SchemeResponseDto getSchemeById(Long schemeId) {

        logger.info("Fetching scheme with ID: {}", schemeId);

        if (schemeId == null) {
            logger.error("Scheme ID is null");
            throw new IllegalArgumentException("Scheme ID cannot be null");
        }
        Scheme scheme = schemeRepository.findById(schemeId)
                .orElseThrow(() -> {
                    return new RuntimeException("Scheme not found with id: " + schemeId);
                });

        logger.info("Scheme fetched successfully with ID: {}", schemeId);
        return SchemeMapper.toDto(scheme);
    }

    @Override
    public List<SchemeResponseDto> getAllSchemes() {
        logger.info("Fetching all schemes");

        List<SchemeResponseDto> schemes = schemeRepository.findAll()
                .stream()
                .map(SchemeMapper::toDto)
                .collect(Collectors.toList());

        logger.info("Total schemes found: {}", schemes.size());

        return schemes;
    }

    @Override
    public SchemeResponseDto updateSchemeById(Long schemeId, SchemeRequestDto dto) {

        logger.info("Updating scheme with ID: {}", schemeId);

        Scheme existingScheme = schemeRepository.findById(schemeId)
                .orElseThrow(() ->
                     new UserException("Scheme not found with id: " + schemeId));


        SchemeMapper.updateEntity(existingScheme, dto);
        Scheme updated = schemeRepository.save(existingScheme);

        logger.info("Scheme updated successfully with ID: {}", schemeId);

        return SchemeMapper.toDto(updated);

    }

    @Override
    public void deleteScheme(Long schemeId) {
        logger.info("Deleting scheme with ID: {}", schemeId);

        Scheme existing = schemeRepository.findById(schemeId)
                .orElseThrow(() -> new RuntimeException("Scheme not found with id: " + schemeId));

        schemeRepository.delete(existing);
        logger.info("Scheme deleted successfully with ID: {}", schemeId);
    }
}
