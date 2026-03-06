package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.MobileOtpVerificationDto;
import com.project.kisan_setu.dto.MobileOtpVerificationResponseDto;

public interface MobileVerificationService {
    MobileOtpVerificationResponseDto sendOtp();
    MobileOtpVerificationResponseDto verifyOtp(MobileOtpVerificationDto dto);
}
