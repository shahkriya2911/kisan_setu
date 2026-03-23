package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.RequestDto.ReportSellerRequestDto;
import com.project.kisan_setu.dto.RequestDto.ReviewRequestDto;
import com.project.kisan_setu.dto.ResponseDto.OrderHistoryResponseDto;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.RatingAndReview;
import com.project.kisan_setu.entity.Report;
import com.project.kisan_setu.enums.OrderStatus;
import com.project.kisan_setu.repository.OrderRepository;
import com.project.kisan_setu.repository.RatingReviewRepository;
import com.project.kisan_setu.repository.ReportSellerRepository;
import com.project.kisan_setu.service.OrderHistoryService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderRepository orderRepository;
    private final ValidatorMethods validatorMethods;
    private final RatingReviewRepository ratingReviewRepository;
    private final ReportSellerRepository reportSellerRepository;
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
                .filter(order -> order.getStatus() == OrderStatus.PAYMENT_PENDING
                        || order.getStatus() == OrderStatus.PAID)
                .map(order -> mapToDto(order, userId))
                .toList();
    }

    @Override
    public List<OrderHistoryResponseDto> getSoldOrderHistory() {
        Long userId = validatorMethods.getCurrentUserId();
        return orderRepository.findBySeller_UserId(userId)
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.PAID)
                .map(order -> mapToDto(order, userId))
                .toList();
    }

    @Override
    public String submitSellerReview(Long orderId, ReviewRequestDto requestDto) {
        Long buyerId = validatorMethods.getCurrentUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getBuyer().getUserId().equals(buyerId)) {
            throw new RuntimeException("You are not allowed to review this order");
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("Review allowed only for completed orders");
        }

        if (ratingReviewRepository.existsByOrder_OrderId(orderId)) {
            throw new RuntimeException("Review already submitted for this order");
        }

        RatingAndReview review = new RatingAndReview();
        review.setOrder(order);
        review.setBuyer(order.getBuyer());
        review.setSeller(order.getListing().getSeller());
        review.setRating(requestDto.getRating());
        review.setReview(requestDto.getReview());
        review.setCreatedAt(LocalDateTime.now());

        ratingReviewRepository.save(review);

        return "Review submitted successfully";
    }

    @Override
    public String reportSeller(Long orderId, ReportSellerRequestDto requestDto) {

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

        reportSellerRepository.save(report);

        return "Seller reported successfully";
    }



    private OrderHistoryResponseDto mapToDto(Order order, Long userId) {
        boolean isBuyer = order.getBuyer()!=null && order.getBuyer().getUserId().equals(userId);
        return new OrderHistoryResponseDto(
                order.getOrderId(),
                order.getListing() != null ? order.getListing().getListingId() : null,
                order.getListing() != null && order.getListing().getCrop() != null
                        ? order.getListing().getCrop().getCropName()
                        : null,
                order.getBuyer() != null ? order.getBuyer().getFullName() : null,
                order.getListing() != null && order.getListing().getSeller() != null
                        ? order.getListing().getSeller().getFullName():null,
                order.getQuantity(),
                order.getAmount(),
                isBuyer ? "PURCHASED" : "SOLD",
                getPaymentLabel(order, isBuyer),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getCreatedAt()
        );
    
    }

    private String getPaymentLabel(Order order, boolean isBuyer) {
        if(order.getStatus() == OrderStatus.PAYMENT_PENDING){
            return "PAYMENT_PENDING";
        }
        if(order.getStatus() == OrderStatus.PAID)
        {
            return isBuyer ? "IN_ESCROW" : "RELEASED";
        }
        return order.getStatus().name();
    }
}
