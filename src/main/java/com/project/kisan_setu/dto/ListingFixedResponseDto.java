package com.project.kisan_setu.dto;

import com.project.kisan_setu.dto.ResponseDto.ProductImageResponseDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ListingFixedResponseDto {
    private Long listingId;
    private String cropName;
    private String variety;
    private String grade;
    private BigDecimal quantity;
    private BigDecimal pricePerKg;
    private String unitId;
    private BigDecimal totalBasePrice;
    private String status;
    private String purchaseType;
    private String saleType;
    private LocalDateTime postedOn;
    private String state;
    private String district;
    private String pickupMethod;
    private String storage;
    private List<ProductImageResponseDto> images;
    private LocalDate harvestDate;
    private String packagingType;
    private Long topBid;
}
