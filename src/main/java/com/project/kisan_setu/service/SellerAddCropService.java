package com.project.kisan_setu.service;

import com.project.kisan_setu.entity.SellerAddCrop;

import java.util.List;

public interface SellerAddCropService {

    SellerAddCrop saveCrop(SellerAddCrop crop);

    List<SellerAddCrop> getAllCrops();

    SellerAddCrop getCropById(Long id);
    SellerAddCrop updateCrop(Long id, SellerAddCrop crop);


    void deleteCrop(Long id);
}
