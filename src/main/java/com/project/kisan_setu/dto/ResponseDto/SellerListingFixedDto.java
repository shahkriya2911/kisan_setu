package com.project.kisan_setu.dto.ResponseDto;

import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellerListingFixedDto {
    private Long listingId;
    private String cropName;
    private String variety;
    private String grade;

    private LocalDate harvestDate;

    private BigDecimal totalQuantity;

    private String packaging;
    private String storage;

    private String state;
    private String district;

    private String pickupMethod;

    private BigDecimal fixedPrice;
    private BigDecimal pricePerUnit;

    private BigDecimal moqPricePerUnit;

    private LocalDateTime postedOn;

    private List<ProductImageResponseDto> images;

    private String description;

    private SaleType saleType;
    private PurchaseType purchaseType;
    private BigDecimal quantity;
    private BigDecimal moq;
    private AuctionStatus auctionStatus;
}
