package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.FraudReportRequestDto;
import com.project.kisan_setu.dto.ResponseDto.FraudReportResponseDto;
import com.project.kisan_setu.service.FraudReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/fraudReportController")
@RequiredArgsConstructor
@Tag(name = "Fraud Report Management", description = "Endpoints for fraud report related resources")
public class FraudReportController {
    private final FraudReportService fraudReportService;

    @PostMapping("/report")
    @Operation(summary = "Report fraud method", description = "Used by user to create a fraud report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fraud report created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid fraud report details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<FraudReportResponseDto> reportFraud(
            @Parameter(description = "Fraud report details", required = true)
            @RequestBody FraudReportRequestDto dto) {

        Long currentUserId = 1L; // replace with auth

        return ResponseEntity.ok(
                fraudReportService.createReport(dto, currentUserId)
        );
    }

    @GetMapping("/all")
    @Operation(summary = "Get all fraud reports method", description = "Used to get all fraud reports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fraud reports fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<FraudReportResponseDto>> getAllReports() {
        return ResponseEntity.ok(fraudReportService.getAllReports());
    }

}
