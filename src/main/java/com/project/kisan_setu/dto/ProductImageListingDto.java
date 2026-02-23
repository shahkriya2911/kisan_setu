package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter //getters
@Setter //setters
public class ProductImageListingDto {
    //image info send from frontend
    private Long listingId;
    private MultipartFile file;
    private Boolean isPrimary;
}

