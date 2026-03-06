package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.AadhaarRequestDto;
import com.project.kisan_setu.dto.AadhaarResponseDto;

import java.util.List;

public interface AadhaarVerificationService {

    AadhaarResponseDto submitAadhaar(AadhaarRequestDto dto);
    AadhaarResponseDto getAadhaarStatus();
    AadhaarResponseDto approveAadhaar(Long userId);
    AadhaarResponseDto rejectAadhaar(Long userId);
    List<AadhaarResponseDto> getPendingVerifications();
}
