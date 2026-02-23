package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by jackson
public class ProductListingDto {

    // Product Info
    private String cropName;
    private String variety;
    private String grade;
    private LocalDate harvestDate;
}

