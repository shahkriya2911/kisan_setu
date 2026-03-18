package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ResponseDto.OrderResponseDto;
import com.project.kisan_setu.dto.PartialLotRequestDto;
import com.project.kisan_setu.entity.Order;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface OrderService {
    Order createOrderFromAcceptedBid(Long bidId);

    Order markPaymentSuccess(Long orderId);

    Order confirmOrder(Long orderId, Long buyerId);

    void expirePendingOrders();

    Order getOrder(Long orderId);

    OrderResponseDto partialLot(Long listingId, PartialLotRequestDto requestDto);
    OrderResponseDto wholeLot(Long listingId);
    OrderResponseDto cancelOrder(Long orderId);
}
