package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.PanCardRequestDto;
import com.project.kisan_setu.dto.ResponseDto.PanCardResponseDto;
import com.project.kisan_setu.service.PanCardVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pan")
@RequiredArgsConstructor
public class PanCardVerificationController {
    private final PanCardVerificationService panCardVerificationService;
    private static final Logger logger = LoggerFactory.getLogger(PanCardVerificationController.class);

    @PostMapping
    public ResponseEntity<PanCardResponseDto> submit(
            @ModelAttribute @Valid PanCardRequestDto dto) {
        logger.debug("PAN submission request attempt");
        return ResponseEntity.ok(panCardVerificationService.submitPan(dto));
    }

    @GetMapping("/status")
    public ResponseEntity<PanCardResponseDto> getStatus() {
        logger.debug("PAN status request attempt");
        return ResponseEntity.ok(panCardVerificationService.getPanStatus());
    }

    @PutMapping("/approve/{userId}")
    public ResponseEntity<PanCardResponseDto> approve(@PathVariable Long userId) {
        logger.debug("PAN approve request for userId: {}", userId);
        return ResponseEntity.ok(panCardVerificationService.approvePan(userId));
    }

    @PutMapping("/reject/{userId}")
    public ResponseEntity<PanCardResponseDto> reject(@PathVariable Long userId) {
        logger.debug("PAN reject request for userId: {}", userId);
        return ResponseEntity.ok(panCardVerificationService.rejectPan(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PanCardResponseDto>> getPending() {
        logger.debug("PAN pending verifications request");
        return ResponseEntity.ok(panCardVerificationService.getPendingVerifications());
    }
}
