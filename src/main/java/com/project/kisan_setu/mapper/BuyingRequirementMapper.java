package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.BuyingRequirementResponseDto;
import com.project.kisan_setu.entity.BuyingRequirement;
import org.springframework.stereotype.Component;

@Component
public class BuyingRequirementMapper {

    public BuyingRequirement toEntity(BuyingRequirementRequestDto dto) {
        BuyingRequirement br = new BuyingRequirement();
        br.setCropType(dto.getCropType());
        br.setQuantity(dto.getQuantity());
        br.setMinPrice(dto.getMinPrice());
        br.setMaxPrice(dto.getMaxPrice());
        br.setQualityGrade(dto.getQualityGrade());
        br.setDelieveryLocation(dto.getDelieveryLocation());
        br.setDeadline(dto.getDeadline());
        return br;
    }

    public BuyingRequirementResponseDto toDto(BuyingRequirement br) {
        BuyingRequirementResponseDto dto = new BuyingRequirementResponseDto();
        dto.setId(br.getId());
        dto.setCropType(br.getCropType());
        dto.setQuantity(br.getQuantity());
        dto.setMinPrice(br.getMinPrice());
        dto.setMaxPrice(br.getMaxPrice());
        dto.setQualityGrade(br.getQualityGrade());
        dto.setDelieveryLocation(br.getDelieveryLocation());
        dto.setDeadline(br.getDeadline());
        dto.setCreatedAt(br.getCreatedAt());
        dto.setUpdatedAt(br.getUpdatedAt());
        if (br.getBuyer() != null) {
            dto.setBuyerId(br.getBuyer().getUserId());
            dto.setBuyerName(br.getBuyer().getUserFullName()); // assuming User has getName()
        }
        return dto;
    }
}
