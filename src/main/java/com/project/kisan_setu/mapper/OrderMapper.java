package com.project.kisan_setu.mapper;
import com.project.kisan_setu.dto.ResponseDto.OrderResponseDto;
import com.project.kisan_setu.entity.Order;

public class OrderMapper {
    public static OrderResponseDto toDto(Order order){
        OrderResponseDto dto = new OrderResponseDto();

        dto.setOrderId(order.getOrderId());
        dto.setListingId(order.getListing().getListingId());
        dto.setBuyerId(order.getBuyer().getUserId());
        dto.setSellerId(order.getSeller().getUserId());
        dto.setStatus(order.getStatus().name());
        dto.setEscrowStatus(order.getEscrowStatus() != null ? order.getEscrowStatus().name() : null);
        dto.setOtp(order.getDeliveryOtp());
        dto.setOtpVerified(order.isOtpVerified());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setQuantity(order.getQuantity());
        dto.setTotalBasePrice(order.getTotalBasePrice());
        return dto;
    }
}
