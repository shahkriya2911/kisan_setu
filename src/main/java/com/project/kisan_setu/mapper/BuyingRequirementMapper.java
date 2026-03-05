package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.BuyingRequirementResponseDto;
import com.project.kisan_setu.entity.BuyingRequirement;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.RequirementStatus;

import java.time.LocalDateTime;

public class BuyingRequirementMapper {

    public static BuyingRequirement toEntity(
            BuyingRequirementRequestDto dto, User buyer) {

        BuyingRequirement br = new BuyingRequirement();
        br.setCropName(dto.getCropName());
        br.setVariety(dto.getVariety());
        br.setGrade(dto.getGrade());
        br.setQuantityRequired(dto.getQuantityRequired());
        br.setUnit(dto.getUnit());
        br.setMinPrice(dto.getMinPrice());
        br.setMaxPrice(dto.getMaxPrice());
        br.setState(dto.getState());
        br.setDistrict(dto.getDistrict());
        br.setDeliveryAddress(dto.getDeliveryAddress());
        br.setDeadline(dto.getDeadline());
        br.setUrgency(dto.getUrgency());
        br.setAddtionalNote(dto.getAdditionalNotes());
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
                br.getState(),
                br.getDistrict(),
                br.getDeliveryAddress(),
                br.getDeadline(),
                br.getUrgency(),
                br.getAddtionalNote(),
                br.getBuyer().getFullName()
        );
    }
}