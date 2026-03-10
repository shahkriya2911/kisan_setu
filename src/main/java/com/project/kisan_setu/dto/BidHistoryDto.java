package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidHistoryDto {
    private Long bidId;
    private BigDecimal buyerAmount;
    private LocalDateTime bidTime;
    private String bidderName;
    private String bidHistoryStatus;
}
