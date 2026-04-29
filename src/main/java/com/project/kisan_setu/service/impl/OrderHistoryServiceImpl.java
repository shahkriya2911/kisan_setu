package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.RequestDto.ReportUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.ReviewRequestDto;
import com.project.kisan_setu.dto.ResponseDto.ApiResponseDto;
import com.project.kisan_setu.dto.ResponseDto.OrderHistoryResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ProductImageResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ReportResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ReviewResponseDto;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.RatingAndReview;
import com.project.kisan_setu.entity.Report;
import com.project.kisan_setu.enums.OrderStatus;
import com.project.kisan_setu.enums.ReportStatus;
import com.project.kisan_setu.enums.ReportedBy;
import com.project.kisan_setu.enums.ReviewType;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.RatingReviewMapper;
import com.project.kisan_setu.mapper.ReportResponseMapper;
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

@Service
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderRepository orderRepository;
    private final ValidatorMethods validatorMethods;
    private final RatingReviewRepository ratingReviewRepository;
    private final ReportUserRepository reportUserRepository;
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
    public ApiResponseDto<ReviewResponseDto> submitSellerReview(Long orderId, ReviewRequestDto requestDto) {

        Long buyerId = validatorMethods.getCurrentUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        if (order.getBuyer() == null || !order.getBuyer().getUserId().equals(buyerId)) {
            throw new SecurityException("You are not allowed to review this order");
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Review allowed only for completed orders");
        }

        if (ratingReviewRepository.existsByOrder_OrderIdAndBuyerReview(orderId, true)) {
            return ApiResponseDto.<ReviewResponseDto>builder()
                    .message("Buyer already reviewed")
                    .data(null)
                    .build();
        }
        RatingAndReview review = new RatingAndReview();
        review.setOrder(order);
        review.setBuyer(order.getBuyer());
        review.setSeller(order.getListing() != null ? order.getListing().getSeller() : null);
        review.setRating(requestDto.getRating());
        review.setReview(requestDto.getReview());
        review.setBuyerReview(true);
        review.setSellerReview(false);
        review.setReviewType(ReviewType.BUYER);
        RatingAndReview saved = ratingReviewRepository.save(review);

        return ApiResponseDto.<ReviewResponseDto>builder()
                .message("Seller reviewed successfully")
                .data(RatingReviewMapper.mapReviewToDto(saved))
                .build();
    }
    @Override
    public ApiResponseDto<ReviewResponseDto> submitBuyerReview(Long orderId, ReviewRequestDto requestDto) {

        Long sellerId = validatorMethods.getCurrentUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        if (order.getListing() == null ||
                order.getListing().getSeller() == null ||
                !order.getListing().getSeller().getUserId().equals(sellerId)) {

            throw new SecurityException("You are not allowed to review this order");
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Review allowed only for completed orders");
        }

        if (ratingReviewRepository.existsByOrder_OrderIdAndBuyerReview(orderId, true)) {
            return ApiResponseDto.<ReviewResponseDto>builder()
                    .message("Seller already reviewed")
                    .data(null)
                    .build();
        }

        RatingAndReview review = new RatingAndReview();
        review.setOrder(order);
        review.setBuyer(order.getBuyer());
        review.setSeller(order.getListing().getSeller());
        review.setRating(requestDto.getRating());
        review.setReview(requestDto.getReview());
        review.setBuyerReview(false);
        review.setSellerReview(true);
        review.setReviewType(ReviewType.SELLER);
        RatingAndReview saved = ratingReviewRepository.save(review);

        return ApiResponseDto.<ReviewResponseDto>builder()
                .message("Buyer reviewed successfully")
                .data(RatingReviewMapper.mapReviewToDto(saved))
                .build();
    }

    @Override
    public ApiResponseDto<ReportResponseDto> reportSeller(Long orderId, ReportUserRequestDto requestDto) {

        Long buyerId = validatorMethods.getCurrentUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getBuyer() == null ||
                !order.getBuyer().getUserId().equals(buyerId)) {
            throw new RuntimeException("This buyer is not allowed to report this seller");
        }

        if (reportUserRepository.existsByOrder_OrderIdAndReportedBy(orderId, ReportedBy.BUYER)) {
            return ApiResponseDto.<ReportResponseDto>builder()
                    .message("Seller already reported")
                    .data(null)
                    .build();
        }
        Report report = new Report();
        report.setOrder(order);
        report.setBuyer(order.getBuyer());
        report.setSeller(order.getListing().getSeller());
        report.setReason(requestDto.getReason());
        report.setDescription(requestDto.getDescription());
        report.setReportStatus(ReportStatus.OPEN);
        report.setReportedBy(ReportedBy.BUYER);
        report.setIsBuyerReported(true);
        report.setIsSellerReported(false);

        Report saved = reportUserRepository.save(report);

        ReportResponseDto responseDto =
                ReportResponseMapper.mapReportToDto(saved, true, false);

        return ApiResponseDto.<ReportResponseDto>builder()
                .message("Seller reported successfully")
                .data(responseDto)
                .build();
    }

    @Override
    public ApiResponseDto<ReportResponseDto> reportBuyer(Long orderId, ReportUserRequestDto requestDto) {

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

        if (reportUserRepository.existsByOrder_OrderIdAndReportedBy(orderId, ReportedBy.SELLER)) {
            return ApiResponseDto.<ReportResponseDto>builder()
                    .message("Buyer already reported")
                    .data(null)
                    .build();
        }
        Report report = new Report();
        report.setOrder(order);
        report.setSeller(order.getListing().getSeller());
        report.setBuyer(order.getBuyer());
        report.setReason(requestDto.getReason());
        report.setDescription(requestDto.getDescription());
        report.setReportStatus(ReportStatus.OPEN);
        report.setReportedBy(ReportedBy.SELLER);
        report.setIsSellerReported(true);
        report.setIsBuyerReported(false);
        Report saved = reportUserRepository.save(report);

        ReportResponseDto responseDto =
                ReportResponseMapper.mapReportToDto(saved, false, true);

        return ApiResponseDto.<ReportResponseDto>builder()
                .message("Buyer reported successfully")
                .data(responseDto)
                .build();
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
