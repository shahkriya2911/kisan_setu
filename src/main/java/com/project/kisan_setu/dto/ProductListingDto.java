package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductListingDto {

    // 🔹 Product Info
    private String cropName;
    private String variety;
    private String grade;
    private String harvestDate;
}

