package com.project.kisan_setu.service;
import com.project.kisan_setu.dto.ResponseDto.OrderResponseDto;
import com.project.kisan_setu.dto.PartialLotRequestDto;
import com.project.kisan_setu.entity.Order;

public interface OrderService {
    OrderResponseDto createOrderFromAcceptedBid(Long bidId);

    OrderResponseDto confirmOrder(Long orderId, Long buyerId);

    void rejectOrder(Long orderId);

    Order getOrder(Long orderId);

    OrderResponseDto partialLot(Long listingId, PartialLotRequestDto requestDto);
    OrderResponseDto wholeLot(Long listingId);
    OrderResponseDto cancelOrder(Long orderId);

    String verifyDeliveryOtp(Long orderId, String otp);
}
