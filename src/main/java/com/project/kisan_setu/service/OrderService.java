package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ResponseDto.OrderResponseDto;
import com.project.kisan_setu.dto.PartialLotRequestDto;
import com.project.kisan_setu.entity.Order;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface OrderService {
    OrderResponseDto createOrderFromAcceptedBid(Long bidId);

    OrderResponseDto markPayment(Long orderId);

    OrderResponseDto confirmOrder(Long orderId, Long buyerId);

    void rejectOrder(Long orderId);

    void expirePendingOrders();

    Order getOrder(Long orderId);

    OrderResponseDto partialLot(Long listingId, PartialLotRequestDto requestDto);
    OrderResponseDto wholeLot(Long listingId);
    OrderResponseDto cancelOrder(Long orderId);
    String verifyDeliveryOtp(Long orderId, String otp);
    OrderResponseDto markOutForDelivery(Long orderId);

}
