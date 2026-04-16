package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.BuyingRequirementRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.service.NotificationService;
import com.project.kisan_setu.util.ValidatorMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyerServiceImplTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private ValidatorMethods validatorMethods;

    @Mock
    private BuyingRequirementRepository buyingRequirementRepository;

    @Mock
    private NotificationService notificationService;

    private BuyerServiceImpl buyerService;

    @BeforeEach
    void setUp() {
        buyerService = new BuyerServiceImpl(
                listingRepository,
                bidRepository,
                validatorMethods,
                buyingRequirementRepository,
                notificationService
        );
    }

    @Test
    void getActiveAuctionListingsBuildsLowercasePrefixPattern() {
        Pageable pageable = PageRequest.of(0, 10);
        when(listingRepository.findActiveAuctionListings(
                com.project.kisan_setu.enums.SaleType.AUCTION,
                com.project.kisan_setu.enums.AuctionStatus.ACTIVE,
                7L,
                "rice%"))
                .thenReturn(List.of());

        Page<?> result = buyerService.getActiveAuctionListings(7L, pageable, "  Rice ");

        assertEquals(0, result.getTotalElements());
        verify(listingRepository).findActiveAuctionListings(
                com.project.kisan_setu.enums.SaleType.AUCTION,
                com.project.kisan_setu.enums.AuctionStatus.ACTIVE,
                7L,
                "rice%");
    }

    @Test
    void getActiveFixedListingsSkipsPatternWhenCropNameBlank() {
        Pageable pageable = PageRequest.of(0, 10);
        when(listingRepository.findActiveFixedListings(
                com.project.kisan_setu.enums.SaleType.FIXED,
                com.project.kisan_setu.enums.AuctionStatus.ACTIVE,
                9L,
                null))
                .thenReturn(List.of());

        Page<?> result = buyerService.getActiveFixedListings(9L, pageable, "   ");

        assertEquals(0, result.getTotalElements());
        verify(listingRepository).findActiveFixedListings(
                com.project.kisan_setu.enums.SaleType.FIXED,
                com.project.kisan_setu.enums.AuctionStatus.ACTIVE,
                9L,
                null);
    }
}
