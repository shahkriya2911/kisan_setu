package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.BankAccountRequestDto;
import com.project.kisan_setu.dto.ResponseDto.BankAccountResponseDto;
import com.project.kisan_setu.service.BankAccountVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/verify/bank account")
@RequiredArgsConstructor
public class BankAccountVerificationController {
    private final BankAccountVerificationService bankAccountVerificationService;
    private static final Logger logger = LoggerFactory.getLogger(BankAccountVerificationController.class);

    @PostMapping
    public ResponseEntity<BankAccountResponseDto> submit(@ModelAttribute @Valid BankAccountRequestDto dto)
    {
        logger.debug("Submit bank account request attempt for user");
        logger.info("Bank account submitted successfully");
        return ResponseEntity.ok(bankAccountVerificationService.submitBankAccount(dto));
    }
    @GetMapping("/status")
    public ResponseEntity<BankAccountResponseDto> getStatus() {
        logger.info("Get bank account status request attempt");
        logger.info("Bank account status fetched successfully");
        return ResponseEntity.ok(bankAccountVerificationService.getStatus());
    }

    @PutMapping("/approve/{userId}")
    public ResponseEntity<BankAccountResponseDto> approve(@PathVariable Long userId) {
        logger.debug("Approve bank account request attempt for user with id : {}",userId);
        logger.info("Bank account approved successfully");
        return ResponseEntity.ok(bankAccountVerificationService.approveBank(userId));
    }
    @PutMapping("/reject/{userId}")
    public ResponseEntity<BankAccountResponseDto> reject(@PathVariable Long userId){
        logger.debug("Reject bank account request attempt for user with id : {}",userId);
        logger.info("Bank accounted rejected successfully");
        return ResponseEntity.ok(bankAccountVerificationService.rejectBank(userId));
    }
}
