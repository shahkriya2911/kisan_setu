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
        dto.setQuantity(order.getQuantity());
        dto.setPricePerKg(order.getPricePerKg());
        dto.setAmount(order.getAmount());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }
}
