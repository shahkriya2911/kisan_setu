package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.ResponseDto.OrderResponseDto;
import com.project.kisan_setu.dto.PartialLotRequestDto;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.*;
import com.project.kisan_setu.mapper.OrderMapper;
import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.repository.OrderRepository;
import com.project.kisan_setu.service.NotificationService;
import com.project.kisan_setu.service.OrderService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BidRepository bidRepository;
    private final ListingRepository listingRepository;
    private final ValidatorMethods validatorMethods;
    private final NotificationService notificationService;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(OrderRepository orderRepository, BidRepository bidRepository, ListingRepository listingRepository, ValidatorMethods validatorMethods, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.bidRepository = bidRepository;
        this.listingRepository = listingRepository;
        this.validatorMethods = validatorMethods;
        this.notificationService = notificationService;
    }

    @Transactional
    @Override
    public OrderResponseDto createOrderFromAcceptedBid(Long bidId) {
        Bid bid = bidRepository.findById(bidId).orElseThrow(() -> new RuntimeException("Bid not found"));
        Listing listing = bid.getListing();
        if (listing == null) {
            throw new RuntimeException("Listing not found for bid");
        }
        if (listing.getSaleType() != SaleType.AUCTION) {
            throw new RuntimeException("Order creation is only allowed for auction listings");
        }
        Long currentUserId = validatorMethods.getCurrentUserId();
        if (!listing.getSeller().getUserId().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to accept this bid");
        }
        if (listing.getStatus() != AuctionStatus.ACTIVE) {
            throw new RuntimeException("Listing is not ready for order creation");
        }
        if (orderRepository.existsByAcceptBid(bid)){
            throw new RuntimeException("Order already created through this bid");
        }
        if (bid.getBidStatus() == BidStatus.REJECTED || bid.getBidStatus() == BidStatus.EXPIRED) {
            throw new RuntimeException("Bid is already " + bid.getBidStatus());
        }
        if (bid.getBidStatus() != BidStatus.ACCEPTED) {
            bid.setBidStatus(BidStatus.ACCEPTED);
            bidRepository.save(bid);
        }
        Order order = new Order();
        order.setBuyer(bid.getBuyer());
        order.setSeller(listing.getSeller());
        order.setListing(listing);
        order.setAcceptBid(bid);
        order.setQuantity(listing.getQuantity());
        order.setPricePerKg(bid.getBuyerAmount());
        BigDecimal total = listing.getQuantity().multiply(bid.getBuyerAmount());
        order.setAmount(total);
        order.setCreatedAt(LocalDateTime.now());
        order.setConfirmationDeadline(LocalDateTime.now().plusHours(24));
        order.setStatus(OrderStatus.PENDING_BUYER_CONFIRMATION);
        listing.setStatus(AuctionStatus.PENDING);
        listingRepository.save(listing);
        orderRepository.save(order);
        notificationService.createNotification(
                bid.getBuyer(),
                "Your bid has been accepted. Please confirm purchase.",
                NotificationStatus.BID_ACCEPTED,listing,bid,order
        );
        return OrderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto markPayment(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.PAYMENT_PENDING){
            throw new RuntimeException("Payment not expected");
        }
        order.setStatus(OrderStatus.PAID);
        Listing listing = order.getListing();
        listing.setStatus(AuctionStatus.SOLD);
        listingRepository.save(listing);
        orderRepository.save(order);
        notificationService.createNotification(
                order.getSeller(),
                "Payment received for your listing #" + order.getListing().getListingId(),
                NotificationStatus.PAYMENT_RECEIVED,listing,null,order
        );
        return OrderMapper.toDto(order);
    }

    @Transactional
    @Override
    public OrderResponseDto confirmOrder(Long orderId, Long buyerId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("Order not found"));
        if (!order.getBuyer().getUserId().equals(buyerId)){
            throw new RuntimeException("Unauthorized Buyer");
        }
        if (order.getStatus()!=OrderStatus.PENDING_BUYER_CONFIRMATION){
            throw new RuntimeException("Order cannot be confirmed");
        }
        if (LocalDateTime.now().isAfter(order.getConfirmationDeadline())){
            order.setStatus(OrderStatus.EXPIRED);
            orderRepository.save(order);
            throw new RuntimeException("Order expired");
        }
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        orderRepository.save(order);
        notificationService.createNotification(
                order.getSeller(),
                "Buyer confirmed order for listing #" + order.getListing().getListingId(),
                NotificationStatus.ORDER_CONFIRMED,null,null,order
        );
        return OrderMapper.toDto(order);
    }

    @Transactional
    @Override
    public void rejectOrder(Long orderId){
        Order order = orderRepository.findById(orderId).
                orElseThrow(()->new RuntimeException("Order not found"));
        Long buyerId = validatorMethods.getCurrentUserId();
        if (!order.getBuyer().getUserId().equals(buyerId)){
            throw new RuntimeException("Unauthorized Buyer");
        }
        if (order.getStatus() != OrderStatus.PENDING_BUYER_CONFIRMATION){
            throw new RuntimeException("Order cannot be rejected");
        }
        order.setStatus(OrderStatus.CANCELLED);
        Bid bid = order.getAcceptBid();
        if (bid != null) {
            bid.setBidStatus(BidStatus.REJECTED);
            bidRepository.save(bid);
        }

        Listing listing = order.getListing();
        listing.setStatus(AuctionStatus.ACTIVE);
        listingRepository.save(listing);
        orderRepository.save(order);
        notificationService.createNotification(listing.getSeller(),"Buyer rejected the accepted bid",
                NotificationStatus.ORDER_CANCELLED,listing,bid,order);
    }

    @Override
    public void expirePendingOrders() {
        List<Order> orders = orderRepository.
                findByStatusAndConfirmationDeadlineBefore(OrderStatus.PENDING_BUYER_CONFIRMATION,
                LocalDateTime.now());
        for (Order order : orders){
            order.setStatus(OrderStatus.EXPIRED);
            Bid bid = order.getAcceptBid();
            if (bid != null) {
                bid.setBidStatus(BidStatus.EXPIRED);
                bidRepository.save(bid);
            }
            Listing listing = order.getListing();
            listing.setStatus(AuctionStatus.ACTIVE);
            listingRepository.save(listing);
            notificationService.createNotification(
                    order.getSeller(),
                    "Buyer didn't confirmed order for listing #" + order.getListing().getListingId(),
                    NotificationStatus.ORDER_EXPIRED,listing,bid,order
            );
        }
        orderRepository.saveAll(orders);

    }

    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("Order not found"));
    }

    @Override
    @Transactional
    public OrderResponseDto partialLot(Long listingId, PartialLotRequestDto requestDto) {
        Listing listing = listingRepository.
                findByIdForUpdate(listingId).orElseThrow(()->new RuntimeException("Listing not found"));
        if (listing.getSaleType() != SaleType.FIXED){
            throw new RuntimeException("Listing is not of fixed type");
        }
        if (listing.getStatus() != AuctionStatus.ACTIVE){
            throw new RuntimeException("Listing is not active");
        }
        if (listing.getPurchaseType() != PurchaseType.PARTIAL_ORDER_ALLOWS){
            throw new RuntimeException("Listing is not of partial lot");
        }
        Long buyerId = validatorMethods.getCurrentUserId();
        User buyer = validatorMethods.validateUserById(buyerId);
        if (listing.getSeller().getUserId().equals(buyerId)) {
            throw new RuntimeException("Seller cannot buy own listing");
        }
        BigDecimal quantity = requestDto.getQuantity();
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("Quantity must be greater than 0");
        }
        BigDecimal available = listing.getRemainingQuantity();
        if (available == null){
            available = listing.getQuantity();
            listing.setRemainingQuantity(available);
        }
        if (available == null){
            throw new RuntimeException("Listing quantity not set");
        }
        if (quantity.compareTo(available)>0){
            throw new RuntimeException("Requested quantity exceeds available stock");
        }
        BigDecimal totalPrice = listing.getPricePerKg().multiply(quantity);
        Order order = new Order();
        order.setListing(listing);
        order.setBuyer(buyer);
        order.setSeller(listing.getSeller());
        order.setQuantity(quantity);
        order.setPricePerKg(listing.getPricePerKg());
        order.setAmount(totalPrice);
        order.setStatus(OrderStatus.PAID);
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);

        BigDecimal remaining = available.subtract(quantity);
        listing.setRemainingQuantity(remaining);
        if (remaining.compareTo(BigDecimal.ZERO) == 0){
            listing.setStatus(AuctionStatus.SOLD);
        }
        listingRepository.save(listing);
        notificationService.createNotification(
                listing.getSeller(),
                "Payment received for your listing #" + listing.getListingId(),
                NotificationStatus.PAYMENT_RECEIVED,listing,null,order
        );
        return OrderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto wholeLot(Long listingId) {
        Listing listing = listingRepository.findByIdForUpdate(listingId)
                .orElseThrow(()->new RuntimeException("Listing not found"));
        if (listing.getSaleType() != SaleType.FIXED){
            throw new RuntimeException("Listing is not of fixed type");
        }
        if (listing.getStatus() != AuctionStatus.ACTIVE){
            throw new RuntimeException("Listing is not active");
        }
        if (listing.getPurchaseType() != PurchaseType.WHOLE_LOT_ONLY){
            throw new RuntimeException("Listing is not of whole lot");
        }
        Long buyerId = validatorMethods.getCurrentUserId();
        User buyer = validatorMethods.validateUserById(buyerId);
        if (listing.getSeller().getUserId().equals(buyerId)) {
            throw new RuntimeException("Seller cannot buy own listing");
        }
        BigDecimal available = listing.getRemainingQuantity();
        if (available == null){
            available = listing.getQuantity();
            listing.setRemainingQuantity(available);
        }
        if (available == null || available.compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("Listing is sold");
        }
        BigDecimal totalPrice = listing.getPricePerKg().multiply(available);
        Order order = new Order();
        order.setListing(listing);
        order.setQuantity(available);
        order.setBuyer(buyer);
        order.setSeller(listing.getSeller());
        order.setPricePerKg(listing.getPricePerKg());
        order.setAmount(totalPrice);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        listing.setRemainingQuantity(BigDecimal.ZERO);
        listing.setStatus(AuctionStatus.SOLD);
        listingRepository.save(listing);
        notificationService.createNotification(
                listing.getSeller(),
                "Payment received for your listing #" + listing.getListingId(),
                NotificationStatus.PAYMENT_RECEIVED,listing,null,order
        );
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderResponseDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new RuntimeException("Order not found"));
        if (order.getStatus() == OrderStatus.CANCELLED){
            throw new RuntimeException("Order already cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        Listing listing = order.getListing();
        listing.setStatus(AuctionStatus.ACTIVE);
        listingRepository.save(listing);
        orderRepository.save(order);
        notificationService.createNotification(order.getBuyer(),"Order cancelled by buyer",
                NotificationStatus.ORDER_CANCELLED,listing,null,order);
        return OrderMapper.toDto(order);
    }
}
