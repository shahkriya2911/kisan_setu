package com.project.kisan_setu.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BidHistoryDto {

    private String buyerName;
    private BigDecimal buyerAmountPerKg;
    private BigDecimal totalbuyerAmount;
    private LocalDateTime bidTime;
}