package com.project.kisan_setu.dto.ResponseDto;

import com.project.kisan_setu.enums.PurchaseType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ListingSummaryResponseDto {

    private Long listingId;
    private String cropId;
    private String variety;
    private String grade;
    private BigDecimal quantity;
    private String unitId;

    private BigDecimal pricePerKg;
    private BigDecimal totalBasePrice;
    private BigDecimal currentHighestBid;
    private PurchaseType purchaseType;
    private String saleType;
    private LocalDateTime auctionEndTime;

    private String stateId;
    private String districtId;
    private String sellerName;

    private List<ProductImageResponseDto> images;

}
