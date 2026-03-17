package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.ResponseDto.AdminDashboardResponseDto;
import com.project.kisan_setu.dto.ResponseDto.EscrowGrowthDto;
import com.project.kisan_setu.dto.ResponseDto.TopCommodityDto;
import com.project.kisan_setu.dto.ResponseDto.UserDistributionDto;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.repository.OrderRepository;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final OrderRepository orderRepository;
    @Override
    public AdminDashboardResponseDto getDashboardOverview() {
      long totalSeller = listingRepository.countDistinctSellers();
//      long totalBuyers = orderRepository.countDistinctBuyers();
      long activeListings = listingRepository.countByStatus(AuctionStatus.ACTIVE);
      long activeAuctions = listingRepository.countByStatus(AuctionStatus.ACTIVE);
//      long transactionsCompleted = transactionRepository.countCompletedTransactions();
//      double escrowFundsHolding = transactionRepository.getTotalEscrowHolding();
//      double platformRevenue = transactionRepository.getTotalPlatformRevenue();

        return new AdminDashboardResponseDto(totalSeller,activeListings,activeAuctions);


    }

    @Override
    public List<Object[]> getTopCommodities() {
        return listingRepository.getTopCommodities();
    }

    @Override
    public UserDistributionDto getUserDistribution() {
        Long sellers = listingRepository.countDistinctSellers();
//        Long buyers = orderRepository.countDistinctBuyers();

//        Long both = transactionRepository.countUsersWhoAreBuyerAndSeller();

        return new UserDistributionDto(sellers);
    }

}
