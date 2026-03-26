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
import com.project.kisan_setu.util.OtpGenerator;
import com.project.kisan_setu.util.ValidatorMethods;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BidRepository bidRepository;
    private final ListingRepository listingRepository;
    private final ValidatorMethods validatorMethods;
    private final NotificationService notificationService;
    private final OtpGenerator otpGenerator;

    public OrderServiceImpl(OrderRepository orderRepository, BidRepository bidRepository, ListingRepository listingRepository, ValidatorMethods validatorMethods, NotificationService notificationService, OtpGenerator otpGenerator) {
        this.orderRepository = orderRepository;
        this.bidRepository = bidRepository;
        this.listingRepository = listingRepository;
        this.validatorMethods = validatorMethods;
        this.notificationService = notificationService;
        this.otpGenerator = otpGenerator;
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
        if (listing.getBidAccepted()) {
            throw new RuntimeException("Bid already accepted for this listing");
        }
        if (bid.getBidStatus() != BidStatus.ACCEPTED) {
            bid.setBidStatus(BidStatus.ACCEPTED);
            bid.setAcceptedTime(LocalDateTime.now());
            bidRepository.save(bid);
        }
        List<Bid> otherBids = bidRepository
                .findByListingListingIdAndBidIdNot(
                        listing.getListingId(),
                        bid.getBidId()
                );

        for (Bid otherBid : otherBids) {
            if (otherBid.getBidStatus() != BidStatus.ACCEPTED) {
                otherBid.setBidStatus(BidStatus.OUTBID);
            }
        }

        bidRepository.saveAll(otherBids);
        Order order = new Order();
        order.setBuyer(bid.getBuyer());
        order.setSeller(listing.getSeller());
        order.setListing(listing);
        order.setAcceptBid(bid);
        order.setQuantity(listing.getQuantity());
        order.setAmount(bid.getBuyerAmount());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING_BUYER_CONFIRMATION);
        order.setConfirmationDeadline(LocalDateTime.now().plusHours(24));
        listing.setStatus(AuctionStatus.PENDING);
        listing.setBidAccepted(true);
        listingRepository.save(listing);
        orderRepository.save(order);
        notificationService.createNotification(
                bid.getBuyer(),
                "Your bid has been accepted. Please confirm purchase.",
                NotificationStatus.BID_ACCEPTED, listing, bid, order
        );
        for (Bid otherBid : otherBids) {
            notificationService.createNotification(
                    otherBid.getBuyer(),
                    "You have been outbid for listing " + listing.getListingId(),
                    NotificationStatus.OUTBID,
                    listing,
                    otherBid,
                    null
            );
        }
        return OrderMapper.toDto(order);
    }


    @Transactional
    @Override
    public OrderResponseDto confirmOrder(Long orderId, Long buyerId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getBuyer().getUserId().equals(buyerId)) {
            throw new RuntimeException("Unauthorized Buyer");
        }
        if (order.getStatus() != OrderStatus.PENDING_BUYER_CONFIRMATION) {
            throw new RuntimeException("Order cannot be confirmed");
        }
        if (LocalDateTime.now().isAfter(order.getConfirmationDeadline())) {
            order.setStatus(OrderStatus.EXPIRED);
            orderRepository.save(order);
            throw new RuntimeException("Order expired");
        }
        order.setStatus(OrderStatus.PAYMENT_HELD);
        order.setEscrowStatus(EscrowStatus.HELD);
        String otp = otpGenerator.generateOtp();
        order.setDeliveryOtp(otp);
        order.setOtpGeneratedAt(LocalDateTime.now());
        order.setOtpVerified(false);
        order.setOtpAttempts(0);
        Listing listing = order.getListing();
        listing.setBidAccepted(true);
        listingRepository.save(listing);
        orderRepository.save(order);
        notificationService.markOrderNotificationHandled(orderId);
        notificationService.createNotification(
                order.getBuyer(),
                "Your delivery OTP for order #" + order.getOrderId() + " is " + otp,
                NotificationStatus.DELIVERY_OTP_SENT,
                listing, null, order
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
        Bid nextTopBid = bidRepository
                .findTopByListingListingIdAndBidStatusOrderByBuyerAmountDesc(
                        listing.getListingId(),
                        BidStatus.PENDING
                )
                .orElse(null);
        if (nextTopBid != null){
            listing.setTopBid(nextTopBid.getBuyerAmount());
        }else {
            listing.setTopBid(null);
        }
        listing.setBidAccepted(false);
        listingRepository.save(listing);
        listing.setStatus(AuctionStatus.ACTIVE);
        listingRepository.save(listing);
        orderRepository.save(order);
        notificationService.markOrderNotificationHandled(orderId);
        notificationService.createNotification(listing.getSeller(),"Buyer rejected the accepted bid",
                NotificationStatus.ORDER_CANCELLED,listing,bid,order);
    }

//    @Override
//    public void expirePendingOrders() {
//        List<Order> orders = orderRepository.
//                findByStatusAndConfirmationDeadlineBefore(OrderStatus.PENDING_BUYER_CONFIRMATION,
//                LocalDateTime.now());
//        for (Order order : orders){
//            order.setStatus(OrderStatus.EXPIRED);
//            Bid bid = order.getAcceptBid();
//            if (bid != null) {
//                bid.setBidStatus(BidStatus.EXPIRED);
//                bidRepository.save(bid);
//            }
//            Listing listing = order.getListing();
//            listing.setStatus(AuctionStatus.ACTIVE);
//            listingRepository.save(listing);
//            notificationService.createNotification(
//                    order.getSeller(),
//                    "Buyer didn't confirmed order for listing #" + order.getListing().getListingId(),
//                    NotificationStatus.ORDER_EXPIRED,listing,bid,order
//            );
//        }
//        orderRepository.saveAll(orders);
//
//    }

    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(()->new RuntimeException("Order not found"));
    }

    @Override
    @Transactional
    public OrderResponseDto partialLot(Long listingId, PartialLotRequestDto requestDto) {

        Listing listing = listingRepository
                .findByIdForUpdate(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (listing.getSaleType() != SaleType.FIXED) {
            throw new RuntimeException("Listing is not of fixed type");
        }

        if (listing.getStatus() != AuctionStatus.ACTIVE) {
            throw new RuntimeException("Listing is not active");
        }

        if (listing.getPurchaseType() != PurchaseType.PARTIAL_ORDER_ALLOWS) {
            throw new RuntimeException("Listing is not partial lot");
        }

        Long buyerId = validatorMethods.getCurrentUserId();
        User buyer = validatorMethods.validateUserById(buyerId);

        if (listing.getSeller().getUserId().equals(buyerId)) {
            throw new RuntimeException("Seller cannot buy own listing");
        }

        BigDecimal quantity = requestDto.getQuantity();

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        // MOQ validation
        BigDecimal moq = listing.getMinimumOrderQuantity();
        if (moq != null && quantity.compareTo(moq) < 0) {
            throw new RuntimeException("Minimum order quantity is " + moq);
        }

        BigDecimal available = listing.getQuantity();
        if (available == null) {
            throw new RuntimeException("Listing quantity not set");
        }

        if (quantity.compareTo(available) > 0) {
            throw new RuntimeException("Requested quantity exceeds available stock");
        }

        BigDecimal totalPrice = listing.getPricePerKg().multiply(quantity);


        BigDecimal remaining = available.subtract(quantity);
        listing.setQuantity(remaining);

        if (remaining.compareTo(BigDecimal.ZERO) == 0) {
            listing.setStatus(AuctionStatus.SOLD);
        }

        Order order = new Order();
        order.setListing(listing);
        order.setBuyer(buyer);
        order.setSeller(listing.getSeller());
        order.setQuantity(quantity);
        order.setPricePerKg(listing.getPricePerKg());
        order.setAmount(totalPrice);
        order.setCreatedAt(LocalDateTime.now());

        order.setStatus(OrderStatus.PAYMENT_HELD);
        order.setEscrowStatus(EscrowStatus.HELD);

        String otp = otpGenerator.generateOtp();
        order.setDeliveryOtp(otp);
        order.setOtpGeneratedAt(LocalDateTime.now());
        order.setOtpVerified(false);
        order.setOtpAttempts(0);

        listingRepository.save(listing);
        orderRepository.save(order);


        notificationService.createNotification(
                order.getBuyer(),
                "Your delivery OTP for order #" + order.getOrderId() + " is " + otp,
                NotificationStatus.DELIVERY_OTP_SENT, listing, null, order
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
        BigDecimal available = listing.getQuantity();
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
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setEscrowStatus(EscrowStatus.HELD);

        String otp = otpGenerator.generateOtp();
        order.setDeliveryOtp(otp);
        order.setOtpGeneratedAt(LocalDateTime.now());
        order.setOtpVerified(false);
        order.setOtpAttempts(0);

        listing.setQuantity(BigDecimal.ZERO);
        listing.setStatus(AuctionStatus.SOLD);

        listingRepository.save(listing);
        orderRepository.save(order);

        notificationService.createNotification(
                order.getBuyer(),
                "Your delivery OTP for order #" + order.getOrderId() + " is " + otp,
                NotificationStatus.DELIVERY_OTP_SENT, listing, null, order
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
        if (order.getStatus() == OrderStatus.PAYMENT_HELD
                || order.getStatus() == OrderStatus.OUT_FOR_DELIVERY
                || order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Order cannot be cancelled after payment is held");
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

    @Override
    @Transactional
    public OrderResponseDto markOutForDelivery(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Long sellerId = validatorMethods.getCurrentUserId();
        if (!order.getSeller().getUserId().equals(sellerId)) {
            throw new RuntimeException("Unauthorized seller");
        }
        if (order.getStatus() != OrderStatus.PAYMENT_HELD) {
            throw new RuntimeException("Order is not ready for delivery");
        }
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.save(order);
        notificationService.createNotification(
                order.getBuyer(),
                "Your order #" + order.getOrderId() + " is out for delivery.",
                NotificationStatus.OUT_FOR_DELIVERY,
                order.getListing(),
                null,
                order
        );
        return OrderMapper.toDto(order);
    }

    @Transactional
    @Override
    public String verifyDeliveryOtp(Long orderId, String otp) {
        Order order = orderRepository.findById(orderId).
                orElseThrow(()->new RuntimeException("Order not found"));
        if (order.isOtpVerified()){
            throw new RuntimeException("Otp already used");
        }
        if (order.getStatus() != OrderStatus.PAYMENT_HELD
                && order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
            throw new RuntimeException("Order is not ready for delivery confirmation");
        }
        if (order.getDeliveryOtp() == null || order.getOtpGeneratedAt() == null) {
            throw new RuntimeException("OTP not generated for this order");
        }
        if (order.getOtpGeneratedAt().plusHours(24).isBefore(LocalDateTime.now())){
            throw new RuntimeException("Otp expired");
        }
        if (!order.getDeliveryOtp().equals(otp)) {

            int attempts = (order.getOtpAttempts() == null ? 0 : order.getOtpAttempts()) + 1;
            order.setOtpAttempts(attempts);
            orderRepository.save(order);

            if (attempts >= 5) {
                throw new RuntimeException("Too many invalid attempts");
            }

            throw new RuntimeException("Invalid OTP");
        }
        order.setOtpVerified(true);

        order.setStatus(OrderStatus.COMPLETED);

        order.setEscrowStatus(EscrowStatus.RELEASED);

        orderRepository.save(order);

        notificationService.createNotification(
                order.getSeller(),
                "Payment released for order #" + order.getOrderId(),
                NotificationStatus.PAYMENT_RELEASED,
                order.getListing(),
                null,
                order
        );
        return "Delivery confirmed, payment released.";
    }
}
