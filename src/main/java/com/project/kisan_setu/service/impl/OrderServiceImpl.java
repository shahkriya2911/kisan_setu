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
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);


    @Transactional
    @Override
    //Seller accepts bid , update if not
    public Order createOrderFromAcceptedBid(Long bidId) {
        Bid bid = bidRepository.findById(bidId).orElseThrow(() -> new RuntimeException("Bid not found"));
        Listing listing = bid.getListing();
        if (listing.getStatus() != AuctionStatus.ACTIVE) {
            throw new RuntimeException("Listing is not active for order creation");
        }
        if (bid.getBidStatus() != BidStatus.ACCEPTED) {
            throw new RuntimeException("Order can only be created from an accepted bid");
        }
        if (orderRepository.existsByAcceptBid(bid)){
            throw new RuntimeException("Order already created through this bid");
        }
        listing = bid.getListing();
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
        listing.setStatus(AuctionStatus.BID_ACCEPTED);
        listingRepository.save(listing);
        return orderRepository.save(order);

    }

    @Override
    @Transactional
    public Order markPaymentSuccess(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("Bid not found"));
        order.setStatus(OrderStatus.PAID);
        Listing listing = order.getListing();
        listing.setStatus(AuctionStatus.SOLD);
        listingRepository.save(listing);
        return orderRepository.save(order);
    }

    @Transactional
    @Override
    //Buyer confirms order -> status becomes PAYMENT_PENDING.
    public Order confirmOrder(Long orderId, Long buyerId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("Bid not found"));
        if (!order.getBuyer().getUserId().equals(buyerId)){
            throw new RuntimeException("Buyer who placed this bid cannot confirm this order");
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
        return orderRepository.save(order);
    }

    @Override
    public void expirePendingOrders() {
        List<Order> orders = orderRepository.
                findByStatusAndConfirmationDeadlineBefore(OrderStatus.PENDING_BUYER_CONFIRMATION,
                LocalDateTime.now());
        for (Order order : orders){
            order.setStatus(OrderStatus.EXPIRED);
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
        BigDecimal quantity = requestDto.getQuantity();
        if (quantity.compareTo(listing.getQuantity())>0){
            throw new RuntimeException("Requested quantity exceeds available stock");
        }
        Long buyerId = validatorMethods.getCurrentUserId();
        User buyer = validatorMethods.validateUserById(buyerId);
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

        BigDecimal remaining = listing.getQuantity().subtract(quantity);
        listing.setQuantity(remaining);
        if (remaining.compareTo(BigDecimal.ZERO) == 0){
            listing.setStatus(AuctionStatus.SOLD);
        }
        listingRepository.save(listing);
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
        BigDecimal totalPrice = listing.getPricePerKg().multiply(listing.getQuantity());
        Order order = new Order();
        order.setListing(listing);
        order.setQuantity(listing.getQuantity());
        order.setBuyer(buyer);
        order.setSeller(listing.getSeller());
        order.setPricePerKg(listing.getPricePerKg());
        order.setAmount(totalPrice);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        listing.setStatus(AuctionStatus.SOLD);
        listingRepository.save(listing);
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderResponseDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.CANCELLED){
            throw new RuntimeException("Order already cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return OrderMapper.toDto(order);
    }
}
