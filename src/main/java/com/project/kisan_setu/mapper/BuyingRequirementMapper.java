package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.BuyingRequirementResponseDto;
import com.project.kisan_setu.entity.BuyingRequirement;
import com.project.kisan_setu.entity.User;

import java.time.LocalDateTime;

public class BuyingRequirementMapper {

    public static BuyingRequirement toEntity(
            BuyingRequirementRequestDto dto, User buyer) {

        BuyingRequirement br = new BuyingRequirement();
        br.setCropType(dto.getCropType());
        br.setQuantityRequired(dto.getQuantityRequired());
        br.setUnit(dto.getUnit());
        br.setMinPrice(dto.getMinPrice());
        br.setMaxPrice(dto.getMaxPrice());
        br.setQualityGrade(dto.getQualityGrade());
        br.setDeliveryLocation(dto.getDeliveryLocation());
        br.setDeadline(dto.getDeadline());
        br.setAdditionalNotes(dto.getAdditionalNotes());
        br.setBuyer(buyer);
        br.setCreatedAt(LocalDateTime.now());

        return br;
    }

    public static BuyingRequirementResponseDto toDto(BuyingRequirement br) {
        return new BuyingRequirementResponseDto(
                br.getRequirementId(),
                br.getCropType(),
                br.getQuantityRequired(),
                br.getDeliveryLocation(),
                br.getDeadline()
        );
    }
}