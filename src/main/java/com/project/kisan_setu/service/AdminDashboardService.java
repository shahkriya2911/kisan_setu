package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ResponseDto.AdminDashboardResponseDto;
import com.project.kisan_setu.dto.ResponseDto.EscrowGrowthDto;
import com.project.kisan_setu.dto.ResponseDto.TopCommodityDto;
import com.project.kisan_setu.dto.ResponseDto.UserDistributionDto;

import java.util.List;

public interface AdminDashboardService {

    AdminDashboardResponseDto getDashboardOverview();
//    List<DailyTransactionDto> getDailyTransactions();
      List<Object[]> getTopCommodities();
     UserDistributionDto getUserDistribution();
//    List<EscrowGrowthDto> getEscrowGrowth();
}
