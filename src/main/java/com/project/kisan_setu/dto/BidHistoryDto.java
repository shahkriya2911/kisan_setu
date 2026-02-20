package com.project.kisan_setu.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BidHistoryDto {

    private String buyerName;
    private BigDecimal bidAmountPerKg;
    private BigDecimal totalBidAmount;
    private LocalDateTime bidTime;
}