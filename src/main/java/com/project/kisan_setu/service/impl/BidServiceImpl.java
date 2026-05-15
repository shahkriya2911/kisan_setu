package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.ResponseDto.MyBiddingsResponseDto;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.BidStatus;
import com.project.kisan_setu.enums.OrderStatus;
import com.project.kisan_setu.mapper.BidMapper;
import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.OrderRepository;
import com.project.kisan_setu.service.BidService;
import com.project.kisan_setu.util.ValidatorMethods;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

@Service
public class BidServiceImpl implements BidService {

    private static final EnumSet<OrderStatus> ACCEPTED_BID_VISIBLE_ORDER_STATUSES =
            EnumSet.of(OrderStatus.PENDING_BUYER_CONFIRMATION, OrderStatus.PAYMENT_PENDING);
    private static final EnumSet<AuctionStatus> ACCEPTED_BID_VISIBLE_LISTING_STATUSES =
            EnumSet.of(AuctionStatus.ACTIVE, AuctionStatus.PENDING);

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
                                .findByBuyerUserIdAndBidStatusInOrderByCreatedAtDesc(buyerId, statuses);
                Map<Long,Bid> latestBid = new LinkedHashMap<>();
                for (Bid bid : getAllBids){
                    Long listingId = bid.getListing().getListingId();
                    latestBid.putIfAbsent(listingId,bid);
                }
                return latestBid.values().stream()
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
            List<Bid> getAllPendingBids = bidRepository
                    .findByBuyerUserIdAndBidStatusOrderByCreatedAtDesc(buyerId, BidStatus.PENDING);
            Map<Long,Bid> latestBid = new LinkedHashMap<>();
            for (Bid bid : getAllPendingBids){
                Long listingId = bid.getListing().getListingId();
                latestBid.putIfAbsent(listingId,bid);
            }
                return latestBid.values().stream()
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
                                .map(this::toAcceptedBidResponse)
                                .flatMap(Optional::stream)
                                .toList();
        }

        @Override
        public List<MyBiddingsResponseDto> getMyRejectedBids() {
                Long buyerId = validatorMethods.getCurrentUserId();
                List<Bid> rejectBids = bidRepository
                                .findByBuyerUserIdAndBidStatusOrderByCreatedAtDesc(buyerId, BidStatus.REJECTED);
                Map<Long, Bid> latestRejectedBidByListing = new LinkedHashMap<>();
                for (Bid bid : rejectBids) {
                        latestRejectedBidByListing.putIfAbsent(bid.getListing().getListingId(), bid);
                }
                return latestRejectedBidByListing.values().stream()
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

        private Optional<MyBiddingsResponseDto> toAcceptedBidResponse(Bid bid) {
                Optional<Order> orderOpt = orderRepository.findByAcceptBid(bid);
                if (orderOpt.isEmpty()) {
                        return Optional.empty();
                }

                Order order = orderOpt.get();
                if (!shouldShowAcceptedBid(order, bid)) {
                        return Optional.empty();
                }

                BigDecimal highestBid = bidRepository
                                .findTopByListingListingIdOrderByBuyerAmountDesc(
                                                bid.getListing().getListingId())
                                .map(Bid::getBuyerAmount)
                                .orElse(BigDecimal.ZERO);

                return Optional.of(BidMapper.toResponse(bid, highestBid, order.getOrderId()));
        }

        private boolean shouldShowAcceptedBid(Order order, Bid bid) {
                if (!ACCEPTED_BID_VISIBLE_ORDER_STATUSES.contains(order.getStatus())) {
                        return false;
                }

                AuctionStatus listingStatus = bid.getListing() != null ? bid.getListing().getStatus() : null;
                return listingStatus != null && ACCEPTED_BID_VISIBLE_LISTING_STATUSES.contains(listingStatus);
        }
}
