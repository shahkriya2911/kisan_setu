package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.RequestDto.ReportUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.ReviewRequestDto;
import com.project.kisan_setu.dto.ResponseDto.OrderHistoryResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ProductImageResponseDto;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.RatingAndReview;
import com.project.kisan_setu.entity.Report;
import com.project.kisan_setu.enums.OrderStatus;
import com.project.kisan_setu.enums.ReportStatus;
import com.project.kisan_setu.enums.ReviewType;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.repository.OrderRepository;
import com.project.kisan_setu.repository.RatingReviewRepository;
import com.project.kisan_setu.repository.ReportUserRepository;
import com.project.kisan_setu.service.InvoiceService;
import com.project.kisan_setu.service.OrderHistoryService;
import com.project.kisan_setu.util.ValidatorMethods;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor

public class OrderHistoryServiceImpl implements OrderHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final ValidatorMethods validatorMethods;
    private final RatingReviewRepository ratingReviewRepository;
    private final ReportUserRepository reportSellerRepository;
    private final InvoiceService invoiceService;
    private static final Logger log = LoggerFactory.getLogger(OrderHistoryServiceImpl.class);
    @Override
    public List<OrderHistoryResponseDto> getAllOrderHistory() {
        Long userId = validatorMethods.getCurrentUserId();
            return orderRepository.findByBuyer_UserIdOrSeller_UserId(userId, userId)
                    .stream()
                    .map(order -> mapToDto(order, userId))
                    .toList();

    }
    @Override
    public List<OrderHistoryResponseDto> getPurchasedOrderHistory() {
        Long userId = validatorMethods.getCurrentUserId();;
        return orderRepository.findByBuyer_UserId(userId)
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.PAYMENT_HELD
                        || order.getStatus() == OrderStatus.COMPLETED)
                .map(order -> mapToDto(order, userId))
                .toList();
    }

    @Transactional
    @Override
    public List<OrderHistoryResponseDto> getSoldOrderHistory() {
        Long userId = validatorMethods.getCurrentUserId();
        log.info("Fetching sold order history for userId: {}", userId);

        List<Order> orders = orderRepository.findBySeller_UserId(userId);
        log.info("Total orders found: {}", orders.size());

        return orders.stream()
                .peek(order -> log.debug(
                        "OrderId: {}, Status: {}, Listing: {}, SaleType: {}",
                        order.getOrderId(),
                        order.getStatus(),
                        order.getListing(),
                        order.getListing() != null ? order.getListing().getSaleType() : "N/A"
                ))
                .filter(order ->
                        (order.getStatus() == OrderStatus.COMPLETED ||
                                order.getStatus() == OrderStatus.PAYMENT_HELD) &&
                                order.getListing() != null
                )
                .map(order -> mapToDto(order, userId))
                .toList();
    }

    @Override
    public String submitSellerReview(Long orderId, ReviewRequestDto requestDto) {

        Long buyerId = validatorMethods.getCurrentUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));


        if (!order.getBuyer().getUserId().equals(buyerId)) {
            throw new SecurityException("You are not allowed to review this order");
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Review allowed only for completed orders");
        }


        if (ratingReviewRepository.existsByOrder_OrderIdAndReviewType(orderId, ReviewType.BUYER)) {
            throw new IllegalStateException("Buyer already reviewed");
        }

        RatingAndReview review = new RatingAndReview();
        review.setOrder(order);
        review.setBuyer(order.getBuyer()); // reviewer
        review.setSeller(order.getListing().getSeller()); // target
        review.setRating(requestDto.getRating());
        review.setReview(requestDto.getReview());
        review.setReviewType(ReviewType.BUYER);

        ratingReviewRepository.save(review);

        return "Seller reviewed successfully";
    }
    @Override
    public String submitBuyerReview(Long orderId, ReviewRequestDto requestDto) {

        Long sellerId = validatorMethods.getCurrentUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        if (!order.getListing().getSeller().getUserId().equals(sellerId)) {
            throw new SecurityException("You are not allowed to review this order");
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Review allowed only for completed orders");
        }

        if (ratingReviewRepository.existsByOrder_OrderIdAndReviewType(orderId, ReviewType.SELLER)) {
            throw new IllegalStateException("Seller already reviewed");
        }

        RatingAndReview review = new RatingAndReview();
        review.setOrder(order);
        review.setBuyer(order.getBuyer());
        review.setSeller(order.getListing().getSeller());
        review.setRating(requestDto.getRating());
        review.setReview(requestDto.getReview());
        review.setReviewType(ReviewType.SELLER);

        ratingReviewRepository.save(review);

        return "Buyer reviewed successfully";
    }

    @Override
    public String reportSeller(Long orderId, ReportUserRequestDto requestDto) {

        Long buyerId = validatorMethods.getCurrentUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getBuyer() == null || !order.getBuyer().getUserId().equals(buyerId)) {
            throw new RuntimeException("This buyer is not allowed to report this seller");
        }

        if (order.getListing() == null || order.getListing().getSeller() == null) {
            throw new RuntimeException("Seller not found for this order");
        }

        Report report = new Report();
        report.setOrder(order);
        report.setBuyer(order.getBuyer());
        report.setSeller(order.getListing().getSeller());
        report.setReason(requestDto.getReason());
        report.setDescription(requestDto.getDescription());

        report.setReportStatus(ReportStatus.OPEN);

        reportSellerRepository.save(report);

        return "Seller reported successfully";
    }

    @Override
    public String reportBuyer(Long orderId, ReportUserRequestDto requestDto) {

        Long sellerId = validatorMethods.getCurrentUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getListing() == null ||
                order.getListing().getSeller() == null ||
                !order.getListing().getSeller().getUserId().equals(sellerId)) {

            throw new SecurityException("This seller is not allowed to report this buyer");
        }

        if (order.getBuyer() == null) {
            throw new NoSuchElementException("Buyer not found for this order");
        }

        Report report = new Report();
        report.setOrder(order);
        report.setSeller(order.getListing().getSeller());
        report.setBuyer(order.getBuyer());

        report.setReason(requestDto.getReason());
        report.setDescription(requestDto.getDescription());
        report.setReportStatus(ReportStatus.OPEN);

        reportSellerRepository.save(report);

        return "Buyer reported successfully";
    }


    @Override
    public byte[] downloadInvoice(Long orderId) {
        Long currentUserId = validatorMethods.getCurrentUserId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new UserException("Order not found", HttpStatus.NOT_FOUND));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new UserException("Invoice is available only for completed orders",HttpStatus.BAD_REQUEST);
        }

        boolean isBuyer = order.getBuyer() != null && order.getBuyer().getUserId().equals(currentUserId);
        boolean isSeller = order.getSeller() != null && order.getSeller().getUserId().equals(currentUserId);
        if (!isBuyer && !isSeller) {
            throw new UserException("Unauthorized: You cannot access this invoice",HttpStatus.FORBIDDEN);
        }

        return invoiceService.generateInvoice(order);
    }



    private OrderHistoryResponseDto mapToDto(Order order, Long userId) {
        boolean isBuyer = order.getBuyer()!=null && order.getBuyer().getUserId().equals(userId);
        return new OrderHistoryResponseDto(
                order.getOrderId(),
                order.getListing() != null ? order.getListing().getListingId() : null,
                order.getListing() != null && order.getListing().getImages() != null
                ? order.getListing().getImages().stream()
                                .map(listingImage -> new ProductImageResponseDto(
                                        listingImage.getFileName(),
                                        listingImage.getFilePath(),
                                        listingImage.getFileType(),
                                        listingImage.getIsPrimary()
                                )).toList():null,
                order.getListing() != null && order.getListing().getCrop() != null
                        ? order.getListing().getCrop().getCropName()
                        : null,
                order.getListing() != null && order.getListing().getVariety() != null
                        ? order.getListing().getVariety()
                        : null,
                order.getBuyer() != null ? order.getBuyer().getFullName() : null,
                order.getListing() != null && order.getListing().getSeller() != null
                        ? order.getListing().getSeller().getFullName():null,
                order.getQuantity(),
                order.getAmount(),
                order.getListing() != null && order.getListing().getState() != null
                        ? order.getListing().getState().getName()
                        : null,
                order.getListing() != null && order.getListing().getDistrict() != null
                        ? order.getListing().getDistrict().getName()
                        : null,
                isBuyer ? "PURCHASED" : "SOLD",
                !isBuyer ? null : order.getDeliveryOtp(),
                getPaymentLabel(order, isBuyer),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getCreatedAt()
        );
    
    }

    private String getPaymentLabel(Order order, boolean isBuyer) {
        if(order.getStatus() == OrderStatus.PAYMENT_PENDING){
            return "PAYMENT_PENDING";
        }
        if(order.getStatus() == OrderStatus.COMPLETED)
        {
            return "RELEASED";
        }
        return order.getStatus().name();
    }

}
