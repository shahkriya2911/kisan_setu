package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.ResponseDto.BuyerListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ListingChangeEventResponseDto;
import com.project.kisan_setu.service.BidService;
import com.project.kisan_setu.service.BuyerService;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyerControllerTest {

    @Mock
    private BuyerService buyerService;

    @Mock
    private BidService bidService;

    @Mock
    private ValidatorMethods validatorMethods;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private BuyerController buyerController;

    @BeforeEach
    void setUp() {
        buyerController = new BuyerController(buyerService, bidService, validatorMethods, messagingTemplate);
    }

    @Test
    void getAuctionListingsUsesSearchAsCropFilterWhenCropNameMissing() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BuyerListingResponseDto> expectedPage = new PageImpl<>(List.of(), pageable, 0);

        when(validatorMethods.getCurrentUserId()).thenReturn(11L);
        when(buyerService.getActiveAuctionListings(11L, pageable, "Rice")).thenReturn(expectedPage);

        Page<BuyerListingResponseDto> actualPage = buyerController.getAuctionListings(null, "  Rice  ", pageable);

        assertSame(expectedPage, actualPage);
        verify(buyerService).getActiveAuctionListings(11L, pageable, "Rice");
    }

    @Test
    void getFixedListingsPrefersCropNameOverSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BuyerListingResponseDto> expectedPage = new PageImpl<>(List.of(), pageable, 0);

        when(validatorMethods.getCurrentUserId()).thenReturn(13L);
        when(buyerService.getActiveFixedListings(13L, pageable, "Wheat")).thenReturn(expectedPage);

        Page<BuyerListingResponseDto> actualPage = buyerController.getFixedListings("  Wheat  ", "Rice", pageable);

        assertSame(expectedPage, actualPage);
        verify(buyerService).getActiveFixedListings(13L, pageable, "Wheat");
    }
}
