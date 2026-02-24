package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private Long activeListings;
    private Long totalBidsReceived;
    private Long pendingApprovals;
    private Long totalRevenue;
}
