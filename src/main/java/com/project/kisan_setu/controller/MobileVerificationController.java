package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.MobileOtpVerificationDto;
import com.project.kisan_setu.dto.MobileOtpVerificationResponseDto;
import com.project.kisan_setu.service.MobileVerificationService;
import com.project.kisan_setu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verify/mobile")
@RequiredArgsConstructor
public class MobileVerificationController {
    private final MobileVerificationService mobileVerificationService;
    @PostMapping("send-otp")
    public ResponseEntity<MobileOtpVerificationResponseDto> sendOtp(){
        return ResponseEntity.ok(mobileVerificationService.sendOtp());
    }
    @PostMapping("verify-otp")
    public ResponseEntity<MobileOtpVerificationResponseDto> verifyOtp(
            @RequestBody MobileOtpVerificationDto dto) {
        return ResponseEntity.ok(mobileVerificationService.verifyOtp(dto));
    }
}
