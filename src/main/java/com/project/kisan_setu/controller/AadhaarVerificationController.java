package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.AadhaarRequestDto;
import com.project.kisan_setu.dto.ResponseDto.AadhaarResponseDto;
import com.project.kisan_setu.service.AadhaarVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/verify/aadhaar")
@RequiredArgsConstructor
public class AadhaarVerificationController {
    private final AadhaarVerificationService aadhaarVerificationService;
    private static final Logger logger = LoggerFactory.getLogger(AadhaarVerificationController.class);
    @PostMapping
    public ResponseEntity<AadhaarResponseDto> submit(@ModelAttribute @Valid AadhaarRequestDto dto) {
        logger.debug("Submit aadhaar request attempt for user");
        logger.info("Aadhaar submitted successfully");
        return ResponseEntity.ok(aadhaarVerificationService.submitAadhaar(dto));
    }
    @GetMapping("/status")
    public ResponseEntity<AadhaarResponseDto> getStatus() {
        logger.info("Get aadhaar status request attempt");
        logger.info("Aadhaar status fetched successfully");
        return ResponseEntity.ok(aadhaarVerificationService.getAadhaarStatus());
    }
    @PutMapping("/approve/{userId}")
    public ResponseEntity<AadhaarResponseDto> approve(@PathVariable Long userId) {
        logger.debug("Approve aadhaar request attempt for user with id : {}",userId);
        logger.info("Aadhaar approved successfully");
        return ResponseEntity.ok(aadhaarVerificationService.approveAadhaar(userId));
    }
    @PutMapping("/reject/{userId}")
    public ResponseEntity<AadhaarResponseDto> reject(@PathVariable Long userId) {
        logger.debug("Reject aadhaar request for user with id : {}",userId);
        logger.info("Aadhaar rejected successfully");
        return ResponseEntity.ok(aadhaarVerificationService.rejectAadhaar(userId));
    }
    @GetMapping("/pending")
    public ResponseEntity<List<AadhaarResponseDto>> getPending() {
        logger.info("Get pending aadhaar request attempt");
        logger.info("Pending aadhaar fetched successfully");
        return ResponseEntity.ok(aadhaarVerificationService.getPendingVerifications());
    }
}
