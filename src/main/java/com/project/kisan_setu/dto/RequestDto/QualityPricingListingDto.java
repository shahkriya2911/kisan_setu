package com.project.kisan_setu.dto.RequestDto;

import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QualityPricingListingDto {

    //quality and pricing info
    @NotNull(message = "quantity is required")
    private BigDecimal quantity;
    @NotNull(message = "unit is required")
    private String unitId;
    @NotNull(message = "price per kg is required")
    private BigDecimal pricePerKg;
    @NotNull(message = "total base price is required")
    private BigDecimal totalBasePrice;
    @Enumerated(EnumType.STRING)
    private PurchaseType purchaseType;
    @NotNull(message = "minimum bid increment is required")
    private BigDecimal minimumBidIncrement;
    @NotNull(message = "sale type is required")
    @NotNull(message = "minimum order quantity is required")
    private BigDecimal minimumOrderQuantity;
    @NotNull(message = "moq price per kg is required")
    private BigDecimal moqPricePerKg;
    @Enumerated(EnumType.STRING)
    private SaleType saleType;
    @NotNull
    private LocalDateTime auctionEndTime;
}
