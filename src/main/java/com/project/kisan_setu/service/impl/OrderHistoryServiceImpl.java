package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.RequestDto.ReportUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.ReviewRequestDto;
import com.project.kisan_setu.dto.ResponseDto.OrderHistoryResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ProductImageResponseDto;
import com.project.kisan_setu.entity.*;
import com.project.kisan_setu.enums.*;
import com.project.kisan_setu.repository.OrderRepository;
import com.project.kisan_setu.repository.OtpRepository;
import com.project.kisan_setu.repository.RatingReviewRepository;
import com.project.kisan_setu.repository.ReportUserRepository;
import com.project.kisan_setu.service.NotificationService;
import com.project.kisan_setu.service.OrderHistoryService;
import com.project.kisan_setu.util.OtpGenerator;
import com.project.kisan_setu.util.ValidatorMethods;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderRepository orderRepository;
    private final ValidatorMethods validatorMethods;
    private final RatingReviewRepository ratingReviewRepository;
    private final ReportUserRepository reportSellerRepository;
    private final OtpGenerator otpGenerator;
    private final OtpRepository otpRepository;
    private final NotificationService notificationService;
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
        System.out.println("Fetching for userId: " + userId);

        List<Order> orders = orderRepository.findBySeller_UserId(userId);
        System.out.println("Total orders found: " + orders.size());

        return orders.stream()
                .peek(order -> System.out.println(
                        "OrderId: " + order.getOrderId() +
                                ", Status: " + order.getStatus() +
                                ", Listing: " + order.getListing() +
                                ", SaleType: " + (order.getListing() != null ? order.getListing().getSaleType() : "N/A")
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
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check buyer ownership
        if (!order.getBuyer().getUserId().equals(buyerId)) {
            throw new RuntimeException("You are not allowed to review this order");
        }

        // Only completed orders
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new RuntimeException("Review allowed only for completed orders");
        }

        if (ratingReviewRepository.existsByOrder_OrderIdAndBuyer_UserId(orderId, buyerId)) {
            throw new RuntimeException("Review already submitted");
        }

        RatingAndReview review = new RatingAndReview();
        review.setOrder(order);
        review.setBuyer(order.getBuyer());
        review.setSeller(order.getListing().getSeller());
        review.setRating(requestDto.getRating());
        review.setReview(requestDto.getReview());
        review.setCreatedAt(LocalDateTime.now());

        ratingReviewRepository.save(review);

        return "Seller reviewed successfully";
    }
    @Override
    public String submitBuyerReview(Long orderId, ReviewRequestDto requestDto) {

        Long sellerId = validatorMethods.getCurrentUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getListing().getSeller().getUserId().equals(sellerId)) {
            throw new RuntimeException("You are not allowed to review this order");
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new RuntimeException("Review allowed only for completed orders");
        }

        if (ratingReviewRepository.existsByOrder_OrderIdAndSeller_UserId(orderId, sellerId)) {
            throw new RuntimeException("Review already submitted");
        }

        RatingAndReview review = new RatingAndReview();
        review.setOrder(order);
        review.setBuyer(order.getBuyer());
        review.setSeller(order.getListing().getSeller());
        review.setRating(requestDto.getRating());
        review.setReview(requestDto.getReview());
        review.setCreatedAt(LocalDateTime.now());

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
        report.setCreatedAt(LocalDateTime.now());

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

            throw new RuntimeException("This seller is not allowed to report this buyer");
        }

        if (order.getBuyer() == null) {
            throw new RuntimeException("Buyer not found for this order");
        }

        Report report = new Report();
        report.setOrder(order);
        report.setSeller(order.getListing().getSeller());
        report.setBuyer(order.getBuyer());

        report.setReason(requestDto.getReason());
        report.setDescription(requestDto.getDescription());
        report.setCreatedAt(LocalDateTime.now());
        report.setReportStatus(ReportStatus.OPEN);

        reportSellerRepository.save(report);

        return "Buyer reported successfully";
    }


    @Override
    public void verifyReceiptOtp(Long orderId, Long buyerId, String otpInput) {
        OrderOtp orderOtp = otpRepository
                .findTopByOrderIdAndBuyerIdOrderByIdDesc(orderId, buyerId)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (orderOtp.getAttempts() >= 3) {
            throw new RuntimeException("Too many attempts. Try again later.");
        }

        if (orderOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!orderOtp.getOtp().equals(otpInput)) {
            orderOtp.setAttempts(orderOtp.getAttempts() + 1);
            otpRepository.save(orderOtp);
            throw new RuntimeException("Invalid OTP");
        }

        if (orderOtp.isVerified()) {
            throw new RuntimeException("OTP already used");
        }
        orderOtp.setVerified(true);
        otpRepository.save(orderOtp);

    }
    public void validateSellerCanDownload(Long orderId, Long buyerId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getBuyer().getUserId().equals(buyerId)) {
            throw new RuntimeException("Unauthorized: Not your order");
        }

        OrderOtp orderOtp =otpRepository
                .findByOrderIdAndBuyerIdAndVerifiedTrue(orderId, buyerId)
                .orElseThrow(() -> new RuntimeException("OTP verification required"));

        if (orderOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired. Request again.");
        }

        orderOtp.setVerified(false);
        otpRepository.save(orderOtp);
    }

    public byte[] generateReceipt(Long orderId) {

        String content = "Receipt for Order ID: " + orderId;

        return content.getBytes();
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
                order.getDeliveryOtp(),
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
            return isBuyer ? "IN_ESCROW" : "RELEASED";
        }
        return order.getStatus().name();
    }

}
