package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.PanCardRequestDto;
import com.project.kisan_setu.dto.ResponseDto.PanCardResponseDto;
import com.project.kisan_setu.service.PanCardVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "PAN Verification", description = "Endpoints for PAN verification related resources")
public class PanCardVerificationController {
    private final PanCardVerificationService panCardVerificationService;
    private static final Logger logger = LoggerFactory.getLogger(PanCardVerificationController.class);

    @PostMapping
    @Operation(summary = "Submit PAN verification method", description = "Used by user to submit PAN verification details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PAN submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid PAN verification details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<PanCardResponseDto> submit(
            @Parameter(description = "PAN verification details", required = true)
            @ModelAttribute @Valid PanCardRequestDto dto) {
        logger.debug("PAN submission request attempt");
        return ResponseEntity.ok(panCardVerificationService.submitPan(dto));
    }

    @GetMapping("/status")
    @Operation(summary = "Get PAN status method", description = "Used by user to get current PAN verification status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PAN status fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<PanCardResponseDto> getStatus() {
        logger.debug("PAN status request attempt");
        return ResponseEntity.ok(panCardVerificationService.getPanStatus());
    }

    @PutMapping("/approve/{userId}")
    @Operation(summary = "Approve PAN verification method", description = "Used to approve PAN verification for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PAN approved successfully"),
            @ApiResponse(responseCode = "404", description = "User or PAN verification not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<PanCardResponseDto> approve(
            @Parameter(description = "User ID request", required = true)
            @PathVariable Long userId) {
        logger.debug("PAN approve request for userId: {}", userId);
        return ResponseEntity.ok(panCardVerificationService.approvePan(userId));
    }

    @PutMapping("/reject/{userId}")
    @Operation(summary = "Reject PAN verification method", description = "Used to reject PAN verification for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PAN rejected successfully"),
            @ApiResponse(responseCode = "404", description = "User or PAN verification not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<PanCardResponseDto> reject(
            @Parameter(description = "User ID request", required = true)
            @PathVariable Long userId) {
        logger.debug("PAN reject request for userId: {}", userId);
        return ResponseEntity.ok(panCardVerificationService.rejectPan(userId));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending PAN verifications method", description = "Used to get all pending PAN verification requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending PAN verifications fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<PanCardResponseDto>> getPending() {
        logger.debug("PAN pending verifications request");
        return ResponseEntity.ok(panCardVerificationService.getPendingVerifications());
    }
}
