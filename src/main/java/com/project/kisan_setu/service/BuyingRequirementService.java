package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.BuyingRequirementResponseDto;
import com.project.kisan_setu.entity.BuyingRequirement;

import java.util.List;

public interface BuyingRequirementService {
    public BuyingRequirementResponseDto createRequirement(BuyingRequirementRequestDto dto);
    public List<BuyingRequirementResponseDto> getAllRequirements();
    public BuyingRequirementResponseDto getRequirementById(Long id);
    public BuyingRequirementResponseDto updateRequirement(Long id, BuyingRequirementRequestDto dto);
    public void deleteRequirement(Long id);
}
