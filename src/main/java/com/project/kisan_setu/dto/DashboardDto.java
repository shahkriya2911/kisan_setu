package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private Long activeListings;
    private Long totalBidsReceived;
    private Long pendingApprovals;
    private BigDecimal totalRevenue;


}
