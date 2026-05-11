package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.ResponseDto.DashboardDto;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.OrderStatus;
import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.BuyingRequirementRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.repository.NotificationRepository;
import com.project.kisan_setu.repository.OrderRepository;
import com.project.kisan_setu.service.FileStorageService;
import com.project.kisan_setu.util.ValidatorMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListingServiceImplTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ValidatorMethods validatorMethods;

    @Mock
    private BuyingRequirementRepository buyingRequirementRepository;

    private ListingServiceImpl listingService;

    @BeforeEach
    void setUp() {
        listingService = new ListingServiceImpl(
                listingRepository,
                bidRepository,
                notificationRepository,
                orderRepository,
                fileStorageService,
                validatorMethods,
                buyingRequirementRepository
        );
    }

    @Test
    void getSellerOverviewCountsDashboardFieldsInCorrectBuckets() {
        User user = new User();
        user.setUserId(4L);

        when(validatorMethods.getCurrentUserId()).thenReturn(4L);
        when(validatorMethods.validateUserById(4L)).thenReturn(user);
        when(listingRepository.countBySeller_UserIdAndStatus(4L, AuctionStatus.ACTIVE)).thenReturn(3L);
        when(bidRepository.countTotalBidsBySellerId(4L)).thenReturn(8L);
        when(bidRepository.countActiveListingsWithPendingBidsBySellerId(4L)).thenReturn(2L);
        when(orderRepository.countByBuyer_UserIdAndStatus(4L, OrderStatus.PENDING_BUYER_CONFIRMATION))
                .thenReturn(5L);
        when(orderRepository.sumAmountBySellerUserIdAndStatusIn(
                4L,
                List.of(OrderStatus.PAYMENT_HELD, OrderStatus.COMPLETED)
        )).thenReturn(new BigDecimal("12500.00"));

        DashboardDto response = listingService.getSellerOverview();

        assertEquals(3L, response.getActiveListings());
        assertEquals(8L, response.getTotalBidsReceived());
        assertEquals(7L, response.getPendingApprovals());
        assertEquals(new BigDecimal("12500.00"), response.getTotalRevenue());
    }
}
