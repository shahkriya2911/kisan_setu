package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.InquiryRequestDto;
import com.project.kisan_setu.dto.InquiryResponseDto;
import com.project.kisan_setu.entity.BuyerInquiry;
import com.project.kisan_setu.enums.InquiryStatus;

import java.util.List;

public interface InquiryService {
    InquiryResponseDto createInquiry(InquiryRequestDto request, String email);
    List<InquiryResponseDto> getSellerInquiries(String email);


}
