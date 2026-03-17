package com.project.kisan_setu.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardResponseDto {
    private long totalSellers;
//    private long totalBuyers;
    private long activeListings;
    private long activeAuctions;


//    private long transactionsCompleted;
//    private BigDecimal escrowFundsHolding;
//    private BigDecimal platformRevenue;


}
