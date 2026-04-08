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
        br.setCrop(crop);
        br.setVariety(dto.getVariety());
        br.setGrade(dto.getGrade());
        br.setQuantityRequired(dto.getQuantityRequired());
        br.setUnit(unit);
        br.setMinPrice(dto.getMinPrice());
        br.setMaxPrice(dto.getMaxPrice());
//        br.setState(state);
//        br.setDistrict(district);
        br.setDeliveryAddress(dto.getDeliveryAddress());
        br.setDeadline(dto.getDeadline());
        br.setUrgency(dto.getUrgency());
        br.setAdditionalNote(dto.getAdditionalNotes());
        br.setBuyer(buyer);
        br.setRequirementStatus(RequirementStatus.OPEN);

        return br;
    }

    public static BuyingRequirementResponseDto toDto(BuyingRequirement br) {
        return new BuyingRequirementResponseDto(
                br.getRequirementId(),
                br.getCrop() != null ? br.getCrop().getCropName() : null,
                br.getVariety(),
                br.getGrade(),
                br.getQuantityRequired(),
                br.getUnit() != null ? br.getUnit().getUnitName() : null,
                br.getMinPrice(),
                br.getMaxPrice(),
                br.getState() != null ? br.getState().getName() : null,
                br.getDistrict() != null ? br.getDistrict().getName() : null,
                br.getDeliveryAddress(),
                br.getDeadline(),
                br.getUrgency(),
                br.getAdditionalNote(),
                br.getBuyer() != null ? br.getBuyer().getFullName() : null
        );
    }
}