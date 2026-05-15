package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.FraudReportRequestDto;
import com.project.kisan_setu.dto.ResponseDto.FraudReportResponseDto;

import java.util.List;

public interface FraudReportService {
    FraudReportResponseDto createReport(FraudReportRequestDto dto,Long currentUserId);
    List<FraudReportResponseDto> getAllReports();
}
