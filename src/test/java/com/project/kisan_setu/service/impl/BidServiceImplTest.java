package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.ResponseDto.MyBiddingsResponseDto;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.CropMaster;
import com.project.kisan_setu.entity.DistrictMaster;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.StateMaster;
import com.project.kisan_setu.entity.UnitMaster;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.BidStatus;
import com.project.kisan_setu.enums.OrderStatus;
import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.OrderRepository;
import com.project.kisan_setu.util.ValidatorMethods;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BidServiceImplTest {

    @Mock
    private BidRepository bidRepository;

    @Mock
    private ValidatorMethods validatorMethods;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void getMyAcceptedBidsReturnsOnlyAcceptedBidsAwaitingPayment() {
        BidServiceImpl bidService = new BidServiceImpl(bidRepository, validatorMethods, orderRepository);

        Bid visibleBid = createAcceptedBid(11L, 101L, AuctionStatus.PENDING, "Wheat");
        Bid paymentPendingBid = createAcceptedBid(12L, 102L, AuctionStatus.PENDING, "Rice");
        Bid paymentHeldBid = createAcceptedBid(13L, 103L, AuctionStatus.PENDING, "Maize");
        Bid completedBid = createAcceptedBid(14L, 104L, AuctionStatus.SOLD, "Cotton");
        Bid expiredBid = createAcceptedBid(15L, 105L, AuctionStatus.EXPIRED, "Mustard");
        Bid flaggedBid = createAcceptedBid(16L, 106L, AuctionStatus.FLAGGED, "Barley");

        when(validatorMethods.getCurrentUserId()).thenReturn(9L);
        when(bidRepository.findByBuyerUserIdAndBidStatusOrderByCreatedAtAsc(9L, BidStatus.ACCEPTED))
                .thenReturn(List.of(visibleBid, paymentPendingBid, paymentHeldBid, completedBid, expiredBid, flaggedBid));

        when(orderRepository.findByAcceptBid(visibleBid))
                .thenReturn(Optional.of(createOrder(701L, OrderStatus.PENDING_BUYER_CONFIRMATION)));
        when(orderRepository.findByAcceptBid(paymentPendingBid))
                .thenReturn(Optional.of(createOrder(702L, OrderStatus.PAYMENT_PENDING)));
        when(orderRepository.findByAcceptBid(paymentHeldBid))
                .thenReturn(Optional.of(createOrder(703L, OrderStatus.PAYMENT_HELD)));
        when(orderRepository.findByAcceptBid(completedBid))
                .thenReturn(Optional.of(createOrder(704L, OrderStatus.COMPLETED)));
        when(orderRepository.findByAcceptBid(expiredBid))
                .thenReturn(Optional.of(createOrder(705L, OrderStatus.PENDING_BUYER_CONFIRMATION)));
        when(orderRepository.findByAcceptBid(flaggedBid))
                .thenReturn(Optional.of(createOrder(706L, OrderStatus.PENDING_BUYER_CONFIRMATION)));

        when(bidRepository.findTopByListingListingIdOrderByBuyerAmountDesc(101L))
                .thenReturn(Optional.of(visibleBid));
        when(bidRepository.findTopByListingListingIdOrderByBuyerAmountDesc(102L))
                .thenReturn(Optional.of(paymentPendingBid));

        List<MyBiddingsResponseDto> response = bidService.getMyAcceptedBids();

        assertEquals(2, response.size());
        assertEquals(List.of(701L, 702L), response.stream().map(MyBiddingsResponseDto::getOrderId).toList());
        assertEquals(List.of(101L, 102L), response.stream().map(MyBiddingsResponseDto::getListingId).toList());
    }

    private Bid createAcceptedBid(Long bidId, Long listingId, AuctionStatus listingStatus, String cropName) {
        User buyer = new User();
        buyer.setUserId(9L);
        buyer.setFullName("Suresh Singh");

        StateMaster state = new StateMaster();
        state.setStateId(1L);
        state.setName("Punjab");

        DistrictMaster district = new DistrictMaster();
        district.setDistrictId(2L);
        district.setName("Ludhiana");
        district.setState(state);

        UnitMaster unit = new UnitMaster();
        unit.setUnitId(3L);
        unit.setUnitName("Kg");

        CropMaster crop = new CropMaster();
        crop.setCropId(4L);
        crop.setCropName(cropName);

        Listing listing = new Listing();
        listing.setListingId(listingId);
        listing.setCrop(crop);
        listing.setVariety("Premium");
        listing.setQuantity(new BigDecimal("100"));
        listing.setPricePerKg(new BigDecimal("25"));
        listing.setState(state);
        listing.setDistrict(district);
        listing.setUnit(unit);
        listing.setMinimumBidIncrement(new BigDecimal("2"));
        listing.setStatus(listingStatus);

        Bid bid = new Bid();
        bid.setBidId(bidId);
        bid.setBuyer(buyer);
        bid.setListing(listing);
        bid.setBuyerAmount(new BigDecimal("2500"));
        bid.setBidStatus(BidStatus.ACCEPTED);
        bid.setCreatedAt(LocalDateTime.now().minusHours(1));
        return bid;
    }

    private Order createOrder(Long orderId, OrderStatus status) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(status);
        return order;
    }
}
