package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.RequestDto.MobileOtpVerificationDto;
import com.project.kisan_setu.dto.ResponseDto.MobileOtpVerificationResponseDto;
import com.project.kisan_setu.service.MobileVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verify/mobile")
@RequiredArgsConstructor
@Tag(name = "Mobile Verification", description = "Endpoints for mobile verification related resources")
public class MobileVerificationController {
    private final MobileVerificationService mobileVerificationService;
    private static final Logger logger = LoggerFactory.getLogger(MobileVerificationController.class);

    @PostMapping("send-otp")
    @Operation(summary = "Send mobile OTP method", description = "Used by user to send a mobile verification OTP")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<MobileOtpVerificationResponseDto> sendOtp(){
        logger.info("Send otp request attempt");
        logger.info("Otp sent successfully");
        return ResponseEntity.ok(mobileVerificationService.sendOtp());
    }

    @PostMapping("verify-otp")
    @Operation(summary = "Verify mobile OTP method", description = "Used by user to verify a mobile OTP")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid OTP details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<MobileOtpVerificationResponseDto> verifyOtp(
            @Parameter(description = "Mobile OTP verification details", required = true)
            @RequestBody MobileOtpVerificationDto dto) {
        logger.info("Verify otp request attempt");
        logger.info("Otp verified successfully");
        return ResponseEntity.ok(mobileVerificationService.verifyOtp(dto));
    }
}
