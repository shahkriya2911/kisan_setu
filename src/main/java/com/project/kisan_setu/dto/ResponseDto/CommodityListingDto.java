package com.project.kisan_setu.dto.ResponseDto;

import com.project.kisan_setu.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommodityListingDto {
    private Long listingId;
    private String commodity;
    private String farmer;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal startingPrice;
    private BigDecimal highestBid;
    private String orderStatus;
    private LocalDateTime postedDate;



}
