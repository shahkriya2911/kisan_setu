package com.project.kisan_setu.dto.ResponseDto;
import com.project.kisan_setu.enums.BidStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyBiddingsResponseDto {
    private String cropName;
    private String variety;
    private BigDecimal quantity;
    private String bidderName;
    private BigDecimal bidAmount;
    private BigDecimal pricePerKg;
    private String state;
    private LocalDateTime bidPlaced;
    private BidStatus bidStatus;
    private List<ProductImageResponseDto> images;
    private BigDecimal currentHighestBid;
    private Long listingId;
    private String unit;
    private String district;
    private BigDecimal minimumBidIncrement;
    private Long orderId;
}
