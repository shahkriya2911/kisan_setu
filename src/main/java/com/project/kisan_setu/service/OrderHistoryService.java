package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.ReportUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.ReviewRequestDto;
import com.project.kisan_setu.dto.ResponseDto.OrderHistoryResponseDto;

import java.util.List;

public interface OrderHistoryService {

    List<OrderHistoryResponseDto> getAllOrderHistory();
    List<OrderHistoryResponseDto> getPurchasedOrderHistory();
    List<OrderHistoryResponseDto> getSoldOrderHistory();
    String submitSellerReview(Long orderId, ReviewRequestDto requestDto);
    String submitBuyerReview(Long orderId, ReviewRequestDto requestDto);
    String reportSeller(Long orderId, ReportUserRequestDto requestDto);
    String reportBuyer(Long orderId, ReportUserRequestDto requestDto);
    byte[] downloadInvoice(Long orderId);


}
