package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.ReportUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.ReviewRequestDto;
import com.project.kisan_setu.dto.ResponseDto.ApiResponseDto;
import com.project.kisan_setu.dto.ResponseDto.OrderHistoryResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ReportResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ReviewResponseDto;

import java.util.List;

public interface OrderHistoryService {

    List<OrderHistoryResponseDto> getAllOrderHistory(String search);
    List<OrderHistoryResponseDto> getPurchasedOrderHistory(String search);
    List<OrderHistoryResponseDto> getSoldOrderHistory(String search);
    ApiResponseDto<ReviewResponseDto> submitSellerReview(Long orderId, ReviewRequestDto requestDto);
    ApiResponseDto<ReviewResponseDto> submitBuyerReview(Long orderId, ReviewRequestDto requestDto);
    ApiResponseDto<ReportResponseDto> reportSeller(Long orderId, ReportUserRequestDto requestDto);
    ApiResponseDto<ReportResponseDto> reportBuyer(Long orderId, ReportUserRequestDto requestDto);
    byte[] downloadInvoice(Long orderId);


}
