package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageResponseDto {

    private Long imageId;
    private String imageUrl;
    private Boolean isPrimary;
}

