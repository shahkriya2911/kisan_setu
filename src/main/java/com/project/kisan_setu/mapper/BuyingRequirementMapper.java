package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.RequestDto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.ResponseDto.BuyingRequirementResponseDto;
import com.project.kisan_setu.entity.*;
import com.project.kisan_setu.enums.RequirementStatus;

import java.time.LocalDateTime;

public class BuyingRequirementMapper {

    public static BuyingRequirement toEntity(
            BuyingRequirementRequestDto dto, User buyer, CropMaster crop, UnitMaster unit, StateMaster state,DistrictMaster district) {

        BuyingRequirement br = new BuyingRequirement();
        br.setCropName(String.valueOf(crop));
        br.setVariety(dto.getVariety());
        br.setGrade(dto.getGrade());
        br.setQuantityRequired(dto.getQuantityRequired());
        br.setUnit(String.valueOf(unit));
        br.setMinPrice(dto.getMinPrice());
        br.setMaxPrice(dto.getMaxPrice());
        br.setDeliveryAddress(dto.getDeliveryAddress());
        br.setDeadline(dto.getDeadline());
        br.setUrgency(dto.getUrgency());
        br.setAdditionalNote(dto.getAdditionalNotes());
        br.setBuyer(buyer);
        br.setCreatedAt(LocalDateTime.now());
        br.setRequirementStatus(RequirementStatus.ACTIVE);

        return br;
    }

    public static BuyingRequirementResponseDto toDto(BuyingRequirement br) {
        return new BuyingRequirementResponseDto(
                br.getRequirementId(),
                br.getCropName(),
                br.getVariety(),
                br.getGrade(),
                br.getQuantityRequired(),
                br.getUnit(),
                br.getMinPrice(),
                br.getMaxPrice(),
                br.getState().getName(),
                br.getDistrict().getName(),
                br.getDeliveryAddress(),
                br.getDeadline(),
                br.getUrgency(),
                br.getAdditionalNote(),
                br.getBuyer().getFullName()
        );
    }
}