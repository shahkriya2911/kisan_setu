package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.MobileOtpVerificationDto;
import com.project.kisan_setu.dto.ResponseDto.MobileOtpVerificationResponseDto;
import com.project.kisan_setu.service.MobileVerificationService;
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
public class MobileVerificationController {
    private final MobileVerificationService mobileVerificationService;
    private static final Logger logger = LoggerFactory.getLogger(MobileVerificationController.class);
    @PostMapping("send-otp")
    public ResponseEntity<MobileOtpVerificationResponseDto> sendOtp(){
        logger.info("Send otp request attempt");
        logger.info("Otp sent successfully");
        return ResponseEntity.ok(mobileVerificationService.sendOtp());
    }
    @PostMapping("verify-otp")
    public ResponseEntity<MobileOtpVerificationResponseDto> verifyOtp(
            @RequestBody MobileOtpVerificationDto dto) {
        logger.info("Verify otp request attempt");
        logger.info("Otp verified successfully");
        return ResponseEntity.ok(mobileVerificationService.verifyOtp(dto));
    }
}
