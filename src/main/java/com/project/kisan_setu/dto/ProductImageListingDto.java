package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductImageListingDto {
    private MultipartFile file;
    private Boolean isPrimary;
}

