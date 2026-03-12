package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.InquiryRequestDto;
import com.project.kisan_setu.dto.ResponseDto.InquiryResponseDto;

import java.util.List;

public interface InquiryService {
    InquiryResponseDto createInquiry(InquiryRequestDto request, Long userId);
    List<InquiryResponseDto> getSellerInquiries(Long userId);


}
