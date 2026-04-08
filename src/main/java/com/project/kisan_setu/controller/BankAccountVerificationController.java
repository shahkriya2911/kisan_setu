package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.BankAccountRequestDto;
import com.project.kisan_setu.dto.ResponseDto.BankAccountResponseDto;
import com.project.kisan_setu.service.BankAccountVerificationService;
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

@RestController
@RequestMapping("api/verify/bank account")
@RequiredArgsConstructor
@Tag(name = "Bank Account Verification", description = "Endpoints for bank account verification related resources")
public class BankAccountVerificationController {
    private final BankAccountVerificationService bankAccountVerificationService;
    private static final Logger logger = LoggerFactory.getLogger(BankAccountVerificationController.class);

    @PostMapping
    @Operation(summary = "Submit bank account verification method", description = "Used by user to submit bank account verification details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bank account submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid bank account verification details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<BankAccountResponseDto> submit(
            @Parameter(description = "Bank account verification details", required = true)
            @ModelAttribute @Valid BankAccountRequestDto dto)
    {
        logger.debug("Submit bank account request attempt for user");
        logger.info("Bank account submitted successfully");
        return ResponseEntity.ok(bankAccountVerificationService.submitBankAccount(dto));
    }

    @GetMapping("/status")
    @Operation(summary = "Get bank account verification status method", description = "Used by user to get current bank account verification status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bank account status fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<BankAccountResponseDto> getStatus() {
        logger.info("Get bank account status request attempt");
        logger.info("Bank account status fetched successfully");
        return ResponseEntity.ok(bankAccountVerificationService.getStatus());
    }

    @PutMapping("/approve/{userId}")
    @Operation(summary = "Approve bank account verification method", description = "Used to approve bank account verification for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bank account approved successfully"),
            @ApiResponse(responseCode = "404", description = "User or bank account verification not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<BankAccountResponseDto> approve(
            @Parameter(description = "User ID request", required = true)
            @PathVariable Long userId) {
        logger.debug("Approve bank account request attempt for user with id : {}",userId);
        logger.info("Bank account approved successfully");
        return ResponseEntity.ok(bankAccountVerificationService.approveBank(userId));
    }

    @PutMapping("/reject/{userId}")
    @Operation(summary = "Reject bank account verification method", description = "Used to reject bank account verification for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bank account rejected successfully"),
            @ApiResponse(responseCode = "404", description = "User or bank account verification not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<BankAccountResponseDto> reject(
            @Parameter(description = "User ID request", required = true)
            @PathVariable Long userId){
        logger.debug("Reject bank account request attempt for user with id : {}",userId);
        logger.info("Bank accounted rejected successfully");
        return ResponseEntity.ok(bankAccountVerificationService.rejectBank(userId));
    }
}
