package com.project.kisan_setu.service;
import com.project.kisan_setu.dto.RequestDto.AadhaarRequestDto;
import com.project.kisan_setu.dto.ResponseDto.AadhaarResponseDto;
import java.util.List;

public interface AadhaarVerificationService {

    AadhaarResponseDto submitAadhaar(AadhaarRequestDto dto);
    AadhaarResponseDto getAadhaarStatus();
    AadhaarResponseDto approveAadhaar(Long userId);
    AadhaarResponseDto rejectAadhaar(Long userId);
    List<AadhaarResponseDto> getPendingVerifications();
}
