package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.RequestDto.PlaceBidRequestDto;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.BuyingRequirement;
import com.project.kisan_setu.entity.CropMaster;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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

    @Test
    void getMyRequirementsUsesTrimmedCropFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        BuyingRequirement requirement = new BuyingRequirement();
        CropMaster crop = new CropMaster();
        crop.setCropName("Rice");
        requirement.setRequirementId(12L);
        requirement.setCrop(crop);

        when(validatorMethods.getCurrentUserId()).thenReturn(5L);
        when(buyingRequirementRepository.findByBuyerUserIdAndCrop_CropNameContainingIgnoreCase(
                5L, "Rice", pageable))
                .thenReturn(new PageImpl<>(List.of(requirement), pageable, 1));

        Page<?> result = buyerService.getMyRequirements(pageable, "  Rice ");

        assertEquals(1, result.getTotalElements());
        verify(buyingRequirementRepository).findByBuyerUserIdAndCrop_CropNameContainingIgnoreCase(
                5L, "Rice", pageable);
    }

    @Test
    void deleteRequirementRemovesOwnedRequirement() {
        BuyingRequirement requirement = new BuyingRequirement();
        User buyer = new User();
        buyer.setUserId(8L);
        requirement.setBuyer(buyer);

        when(validatorMethods.getCurrentUserId()).thenReturn(8L);
        when(buyingRequirementRepository.findById(3L)).thenReturn(Optional.of(requirement));

        buyerService.deleteRequirement(3L);

        verify(buyingRequirementRepository).delete(requirement);
    }

    @Test
    void deleteRequirementRejectsOtherBuyerRequirement() {
        BuyingRequirement requirement = new BuyingRequirement();
        User buyer = new User();
        buyer.setUserId(11L);
        requirement.setBuyer(buyer);

        when(validatorMethods.getCurrentUserId()).thenReturn(8L);
        when(buyingRequirementRepository.findById(4L)).thenReturn(Optional.of(requirement));

        assertThrows(UserException.class, () -> buyerService.deleteRequirement(4L));
    }

    @Test
    void placeBidAllowsAmountWithinConfiguredRange() {
        Listing listing = new Listing();
        listing.setListingId(15L);
        listing.setSaleType(SaleType.AUCTION);
        listing.setPurchaseType(PurchaseType.WHOLE_LOT_ONLY);
        listing.setAuctionEndTime(LocalDateTime.now().plusHours(2));
        listing.setBidAccepted(false);
        listing.setTotalBasePrice(new BigDecimal("1000"));
        listing.setMinimumBidIncrement(new BigDecimal("50"));
        listing.setMaximumBidIncrement(new BigDecimal("100"));

        User seller = new User();
        seller.setUserId(20L);
        listing.setSeller(seller);

        User buyer = new User();
        buyer.setUserId(8L);
        buyer.setFullName("Buyer");

        PlaceBidRequestDto dto = new PlaceBidRequestDto();
        dto.setBuyerAmount(new BigDecimal("1080"));

        when(validatorMethods.getCurrentUserId()).thenReturn(8L);
        when(listingRepository.findByIdForUpdate(15L)).thenReturn(Optional.of(listing));
        when(validatorMethods.validateUserById(8L)).thenReturn(buyer);
        when(bidRepository.findTopByListingListingIdOrderByBidIdDesc(15L)).thenReturn(Optional.empty());
        when(bidRepository.findTopByListingListingIdOrderByBuyerAmountDesc(15L)).thenReturn(Optional.empty());
        when(bidRepository.save(any(Bid.class))).thenAnswer(invocation -> invocation.getArgument(0));

        buyerService.placeBid(15L, dto);

        verify(bidRepository).save(any(Bid.class));
        verify(notificationService).createNotification(
                eq(seller),
                eq("New bid placed on your listing"),
                eq(com.project.kisan_setu.enums.NotificationStatus.BID_PLACED),
                eq(listing),
                any(Bid.class),
                isNull());
    }

    @Test
    void placeBidRejectsAmountOutsideConfiguredRange() {
        Listing listing = new Listing();
        listing.setListingId(15L);
        listing.setSaleType(SaleType.AUCTION);
        listing.setPurchaseType(PurchaseType.WHOLE_LOT_ONLY);
        listing.setAuctionEndTime(LocalDateTime.now().plusHours(2));
        listing.setBidAccepted(false);
        listing.setTotalBasePrice(new BigDecimal("1000"));
        listing.setMinimumBidIncrement(new BigDecimal("50"));
        listing.setMaximumBidIncrement(new BigDecimal("100"));

        User seller = new User();
        seller.setUserId(20L);
        listing.setSeller(seller);

        User buyer = new User();
        buyer.setUserId(8L);

        Bid highestBid = new Bid();
        highestBid.setBuyerAmount(new BigDecimal("1200"));

        PlaceBidRequestDto dto = new PlaceBidRequestDto();
        dto.setBuyerAmount(new BigDecimal("1310"));

        when(validatorMethods.getCurrentUserId()).thenReturn(8L);
        when(listingRepository.findByIdForUpdate(15L)).thenReturn(Optional.of(listing));
        when(validatorMethods.validateUserById(8L)).thenReturn(buyer);
        when(bidRepository.findTopByListingListingIdOrderByBidIdDesc(15L)).thenReturn(Optional.empty());
        when(bidRepository.findTopByListingListingIdOrderByBuyerAmountDesc(15L)).thenReturn(Optional.of(highestBid));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> buyerService.placeBid(15L, dto));

        assertEquals("Bid must be between 1250 and 1300", exception.getMessage());
    }
}
