package com.project.kisan_setu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.kisan_setu.enums.SaleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QualityPricingListingDto {

    //quality and pricing info
    @NotNull(message = "quantity is required")
    private Integer quantity;
    @NotNull(message = "unit is required")
    private String unit;
    @NotNull(message = "price per kg is required")
    private Double pricePerKg;
    @NotNull(message = "total base price is required")
    private Double totalBasePrice;
    @NotBlank(message = "purchase type is required")
    private String purchaseType;
    @NotNull(message = "minimum bid increment is required")
    private Double minimumBidIncrement;
    @NotNull(message = "sale type is required")
    @NotNull(message = "minimum order quantity is required")
    private Integer minimumOrderQuantity;
    @NotNull(message = "moq price per kg is required")
    private Double moqPricePerKg;
    @Enumerated(EnumType.STRING)
    private SaleType saleType;  // FIXED or AUCTION
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime auctionEndTime;
}
