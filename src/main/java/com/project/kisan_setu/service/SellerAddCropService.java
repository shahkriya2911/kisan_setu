package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.SellerAddCropRequestDto;
import com.project.kisan_setu.dto.SellerAddCropResponseDto;
import com.project.kisan_setu.entity.SellerAddCrop;

import java.util.List;

public interface SellerAddCropService {

    SellerAddCropResponseDto saveCrop(SellerAddCropRequestDto crop);

    List<SellerAddCropResponseDto> getAllCrops();
    SellerAddCropResponseDto getCropById(Long id);
    SellerAddCropResponseDto updateCrop(Long id, SellerAddCropRequestDto dto);
     void deleteCrop(Long id);

}
