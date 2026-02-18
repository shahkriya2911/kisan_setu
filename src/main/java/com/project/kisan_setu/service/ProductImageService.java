package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ProductImageListingDto;
import com.project.kisan_setu.entity.ProductImage;

import java.io.IOException;

public interface ProductImageService {
    public ProductImage uploadImage(ProductImageListingDto dto)throws IOException;
    public void deleteImage(Long imageId);
}
