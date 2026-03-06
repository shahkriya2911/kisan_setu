package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.AadhaarRequestDto;
import com.project.kisan_setu.dto.AadhaarResponseDto;
import com.project.kisan_setu.service.AadhaarVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/verify/aadhaar")
@RequiredArgsConstructor
public class AadhaarVerificationController {
    private final AadhaarVerificationService aadhaarVerificationService;

    @PostMapping
    public ResponseEntity<AadhaarResponseDto> submit(@ModelAttribute AadhaarRequestDto dto) {
        return ResponseEntity.ok(aadhaarVerificationService.submitAadhaar(dto));
    }
    @GetMapping("/status")
    public ResponseEntity<AadhaarResponseDto> getStatus() {
        return ResponseEntity.ok(aadhaarVerificationService.getAadhaarStatus());
    }
    @PutMapping("/approve/{userId}")
    public ResponseEntity<AadhaarResponseDto> approve(@PathVariable Long userId) {
        return ResponseEntity.ok(aadhaarVerificationService.approveAadhaar(userId));
    }
    @PutMapping("/reject/{userId}")
    public ResponseEntity<AadhaarResponseDto> reject(@PathVariable Long userId) {
        return ResponseEntity.ok(aadhaarVerificationService.rejectAadhaar(userId));
    }
    @GetMapping("/pending")
    public ResponseEntity<List<AadhaarResponseDto>> getPending() {
        return ResponseEntity.ok(aadhaarVerificationService.getPendingVerifications());
    }
}
