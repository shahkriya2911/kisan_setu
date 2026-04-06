package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.ResponseDto.MyBiddingsResponseDto;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.BidStatus;
import com.project.kisan_setu.mapper.BidMapper;
import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.OrderRepository;
import com.project.kisan_setu.service.BidService;
import com.project.kisan_setu.util.ValidatorMethods;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final ValidatorMethods validatorMethods;
    private final OrderRepository orderRepository;

    public BidServiceImpl(BidRepository bidRepository, ValidatorMethods validatorMethods, OrderRepository orderRepository) {
        this.bidRepository = bidRepository;
        this.validatorMethods = validatorMethods;
        this.orderRepository = orderRepository;
    }

        @Override
        public List<MyBiddingsResponseDto> getAllMyBids() {
                Long buyerId = validatorMethods.getCurrentUserId();
                List<BidStatus> statuses = List.of(BidStatus.PENDING, BidStatus.REJECTED, BidStatus.EXPIRED,
                                BidStatus.ACCEPTED);
                List<Bid> getAllBids = bidRepository
                                .findByBuyerUserIdAndBidStatusInOrderByCreatedAtAsc(buyerId, statuses);
                return getAllBids.stream()
                                .map(bid -> {
                                        BigDecimal highestBid = bidRepository
                                                        .findTopByListingListingIdOrderByBuyerAmountDesc(
                                                                        bid.getListing().getListingId())
                                                        .map(Bid::getBuyerAmount)
                                                        .orElse(BigDecimal.ZERO);
                                        Long orderId = orderRepository.findByAcceptBid(bid)
                                                .map(Order::getOrderId).orElse(null);
                                        return BidMapper.toResponse(bid, highestBid,orderId);
                                }).toList();
        }

        @Override
        public List<MyBiddingsResponseDto> getMyPendingBids() {
                Long buyerId = validatorMethods.getCurrentUserId();
                List<Bid> pendingBids = bidRepository
                                .findByBuyerUserIdAndBidStatusOrderByCreatedAtAsc(buyerId, BidStatus.PENDING);
                return pendingBids.stream()
                                .map(bid -> {
                                        BigDecimal highestBid = bidRepository
                                                        .findTopByListingListingIdOrderByBuyerAmountDesc(
                                                                        bid.getListing().getListingId())
                                                        .map(Bid::getBuyerAmount)
                                                        .orElse(BigDecimal.ZERO);
                                        return BidMapper.toResponse(bid, highestBid,null);
                                }).toList();
        }

        @Override
        public List<MyBiddingsResponseDto> getMyAcceptedBids() {
                Long buyerId = validatorMethods.getCurrentUserId();
                List<Bid> acceptBids = bidRepository.findByBuyerUserIdAndBidStatusOrderByCreatedAtAsc(buyerId,
                                BidStatus.ACCEPTED);
                return acceptBids.stream()
                                .map(bid -> {
                                        BigDecimal highestBid = bidRepository
                                                        .findTopByListingListingIdOrderByBuyerAmountDesc(
                                                                        bid.getListing().getListingId())
                                                        .map(Bid::getBuyerAmount)
                                                        .orElse(BigDecimal.ZERO);
                                        Long orderId = orderRepository
                                                .findByAcceptBid(bid).map(Order::getOrderId).orElse(null);
                                        return BidMapper.toResponse(bid, highestBid,orderId);
                                }).toList();
        }

        @Override
        public List<MyBiddingsResponseDto> getMyRejectedBids() {
                Long buyerId = validatorMethods.getCurrentUserId();
                List<Bid> rejectBids = bidRepository
                                .findByBuyerUserIdAndBidStatusOrderByCreatedAtAsc(buyerId, BidStatus.REJECTED);
                return rejectBids.stream()
                                .map(bid -> {
                                        BigDecimal highestBid = bidRepository
                                                        .findTopByListingListingIdOrderByBuyerAmountDesc(
                                                                        bid.getListing().getListingId())
                                                        .map(Bid::getBuyerAmount)
                                                        .orElse(BigDecimal.ZERO);
                                        return BidMapper.toResponse(bid, highestBid,null);
                                }).toList();
        }

        @Override
        public List<MyBiddingsResponseDto> getMyOutbidBids() {
                Long buyerId = validatorMethods.getCurrentUserId();
                List<Bid> outbidBids = bidRepository
                                .findOutbidBids(buyerId, BidStatus.PENDING, AuctionStatus.EXPIRED);
                return outbidBids.stream()
                                .map(bid -> {
                                        BigDecimal highestBid = bidRepository
                                                        .findTopByListingListingIdOrderByBuyerAmountDesc(
                                                                        bid.getListing().getListingId())
                                                        .map(Bid::getBuyerAmount)
                                                        .orElse(BigDecimal.ZERO);
                                        return BidMapper.toResponse(bid, highestBid,null);
                                }).toList();
        }
}
