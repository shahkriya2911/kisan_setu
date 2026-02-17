package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.SellerAddCropRequestDto;
import com.project.kisan_setu.dto.SellerAddCropResponseDto;
import com.project.kisan_setu.entity.SellerAddCrop;
import org.springframework.stereotype.Component;

@Component
public class SellerAddCropMapper {
    public SellerAddCrop toEntity(SellerAddCropRequestDto dto)
    {
        SellerAddCrop crop = new SellerAddCrop();
        crop.setCropName(dto.getCropName());
        crop.setQuantity(dto.getQuantity());
        crop.setBasePrice(dto.getBasePrice());
        crop.setHarvestDate(dto.getHarvestDate());
        crop.setVillage(dto.getVillage());
        crop.setTaluka(dto.getTaluka());
        crop.setDistrict(dto.getDistrict());
        crop.setState(dto.getState());
        crop.setStatus(dto.getStatus());
        crop.setCreatedAt(java.time.LocalDateTime.now());
        return crop;
    }

    public SellerAddCropResponseDto toDto(SellerAddCrop entity) {
        SellerAddCropResponseDto dto = new SellerAddCropResponseDto();
        dto.setId(entity.getId());
        dto.setCropName(entity.getCropName());
        dto.setQuantity(entity.getQuantity());
        dto.setBasePrice(entity.getBasePrice());
        dto.setHarvestDate(entity.getHarvestDate());
        dto.setVillage(entity.getVillage());
        dto.setTaluka(entity.getTaluka());
        dto.setDistrict(entity.getDistrict());
        dto.setState(entity.getState());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setSellerId(entity.getSeller().getUserId());
        dto.setSellerName(entity.getSeller().getUserFullName());
        return dto;
    }
}
