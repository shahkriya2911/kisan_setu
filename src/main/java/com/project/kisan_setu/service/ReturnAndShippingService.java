package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ReturnAndShippingRequestDto;
import com.project.kisan_setu.dto.ReturnAndShippingResponseDto;

import java.util.List;

public interface ReturnAndShippingService {
    List<ReturnAndShippingResponseDto> getMyReturnAndShipping();
    ReturnAndShippingResponseDto getReturnAndShippingById(Long returnAndShippingId);
    ReturnAndShippingResponseDto postReturnAndShipping(ReturnAndShippingRequestDto returnAndShippingRequestDto);
    ReturnAndShippingResponseDto updateReturnAndShipping(Long returnAndShippingId,ReturnAndShippingRequestDto returnAndShippingRequestDto);

    void deleteReturnAndShipping(Long returnAndShippingId);
}
