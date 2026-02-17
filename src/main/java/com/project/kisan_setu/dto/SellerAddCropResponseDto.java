package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class SellerAddCropResponseDto {
    private Long id;
    private String cropName;
    private Double quantity;
    private Double basePrice;
    private LocalDateTime harvestDate;
    private String village;
    private String taluka;
    private String district;
    private String state;
    private String status;
    private LocalDateTime createdAt;
    private Long sellerId;
    private String sellerName;
}
