package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.ReportSellerRequestDto;
import com.project.kisan_setu.dto.RequestDto.ReviewRequestDto;
import com.project.kisan_setu.dto.ResponseDto.OrderHistoryResponseDto;

import java.util.List;

public interface OrderHistoryService {

    List<OrderHistoryResponseDto> getAllOrderHistory();
    List<OrderHistoryResponseDto> getPurchasedOrderHistory();
    List<OrderHistoryResponseDto> getSoldOrderHistory();
    String submitSellerReview(Long orderId, ReviewRequestDto requestDto);
    String reportSeller(Long orderId, ReportSellerRequestDto requestDto);

}
