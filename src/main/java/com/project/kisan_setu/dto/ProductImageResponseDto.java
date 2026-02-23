package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by jackson
public class ProductImageResponseDto {
    //image response info
    private Long imageId;
    private String imageUrl;
    private Boolean isPrimary;
}

