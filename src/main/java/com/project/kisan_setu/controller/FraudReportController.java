package com.project.kisan_setu.controller;


import com.project.kisan_setu.dto.RequestDto.FraudReportRequestDto;
import com.project.kisan_setu.dto.ResponseDto.FraudReportResponseDto;
import com.project.kisan_setu.service.FraudReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/fraudReportController")
@RequiredArgsConstructor
public class FraudReportController {
    private final FraudReportService fraudReportService;
    @PostMapping("/report")
    public ResponseEntity<FraudReportResponseDto> reportFraud(
            @RequestBody FraudReportRequestDto dto) {

        Long currentUserId = 1L; // replace with auth

        return ResponseEntity.ok(
                fraudReportService.createReport(dto, currentUserId)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<FraudReportResponseDto>> getAllReports() {
        return ResponseEntity.ok(fraudReportService.getAllReports());
    }

}
