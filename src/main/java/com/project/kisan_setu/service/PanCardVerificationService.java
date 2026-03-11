package com.project.kisan_setu.service;
import com.project.kisan_setu.dto.PanCardRequestDto;
import com.project.kisan_setu.dto.PanCardResponseDto;

import java.util.List;

public interface PanCardVerificationService {

    PanCardResponseDto submitPan(PanCardRequestDto dto);
    PanCardResponseDto getPanStatus();
    PanCardResponseDto approvePan(Long userId);
    PanCardResponseDto rejectPan(Long userId);
    List<PanCardResponseDto> getPendingVerifications();
}
