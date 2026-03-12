package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.MobileOtpVerificationDto;
import com.project.kisan_setu.dto.ResponseDto.MobileOtpVerificationResponseDto;

public interface MobileVerificationService {
    MobileOtpVerificationResponseDto sendOtp();
    MobileOtpVerificationResponseDto verifyOtp(MobileOtpVerificationDto dto);
}
