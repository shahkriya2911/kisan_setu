package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.AadhaarRequestDto;
import com.project.kisan_setu.dto.ResponseDto.AadhaarResponseDto;
import com.project.kisan_setu.service.AadhaarVerificationService;
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
@RequestMapping("api/verify/aadhaar")
@RequiredArgsConstructor
@Tag(name = "Aadhaar Verification", description = "Endpoints for Aadhaar verification related resources")
public class AadhaarVerificationController {
    private final AadhaarVerificationService aadhaarVerificationService;
    private static final Logger logger = LoggerFactory.getLogger(AadhaarVerificationController.class);

    @PostMapping
    @Operation(summary = "Submit Aadhaar verification method", description = "Used by user to submit Aadhaar verification details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aadhaar submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Aadhaar verification details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<AadhaarResponseDto> submit(
            @Parameter(description = "Aadhaar verification details", required = true)
            @ModelAttribute @Valid AadhaarRequestDto dto) {
        logger.debug("Submit aadhaar request attempt for user");
        logger.info("Aadhaar submitted successfully");
        return ResponseEntity.ok(aadhaarVerificationService.submitAadhaar(dto));
    }

    @GetMapping("/status")
    @Operation(summary = "Get Aadhaar status method", description = "Used by user to get current Aadhaar verification status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aadhaar status fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<AadhaarResponseDto> getStatus() {
        logger.info("Get aadhaar status request attempt");
        logger.info("Aadhaar status fetched successfully");
        return ResponseEntity.ok(aadhaarVerificationService.getAadhaarStatus());
    }

    @PutMapping("/approve/{userId}")
    @Operation(summary = "Approve Aadhaar verification method", description = "Used to approve Aadhaar verification for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aadhaar approved successfully"),
            @ApiResponse(responseCode = "404", description = "User or Aadhaar verification not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<AadhaarResponseDto> approve(
            @Parameter(description = "User ID request", required = true)
            @PathVariable Long userId) {
        logger.debug("Approve aadhaar request attempt for user with id : {}",userId);
        logger.info("Aadhaar approved successfully");
        return ResponseEntity.ok(aadhaarVerificationService.approveAadhaar(userId));
    }

    @PutMapping("/reject/{userId}")
    @Operation(summary = "Reject Aadhaar verification method", description = "Used to reject Aadhaar verification for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aadhaar rejected successfully"),
            @ApiResponse(responseCode = "404", description = "User or Aadhaar verification not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<AadhaarResponseDto> reject(
            @Parameter(description = "User ID request", required = true)
            @PathVariable Long userId) {
        logger.debug("Reject aadhaar request for user with id : {}",userId);
        logger.info("Aadhaar rejected successfully");
        return ResponseEntity.ok(aadhaarVerificationService.rejectAadhaar(userId));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending Aadhaar verifications method", description = "Used to get all pending Aadhaar verification requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending Aadhaar verifications fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<AadhaarResponseDto>> getPending() {
        logger.info("Get pending aadhaar request attempt");
        logger.info("Pending aadhaar fetched successfully");
        return ResponseEntity.ok(aadhaarVerificationService.getPendingVerifications());
    }
}
