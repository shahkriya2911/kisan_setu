package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.SellerAddCropDto;
import com.project.kisan_setu.entity.SellerAddCrop;

import java.util.List;

public interface SellerAddCropService {
    public SellerAddCropDto createCrop(SellerAddCropDto dto);
    public List<SellerAddCrop> getAllCrops();
    public SellerAddCrop getCropById(Long id);
    public SellerAddCrop updateCrop(Long id, SellerAddCrop updatedCrop);
    public void deleteCrop(Long id);
}
