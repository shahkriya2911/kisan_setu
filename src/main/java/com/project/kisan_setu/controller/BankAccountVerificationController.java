package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.BankAccountRequestDto;
import com.project.kisan_setu.dto.BankAccountResponseDto;
import com.project.kisan_setu.repository.BankAccountVerificationRepository;
import com.project.kisan_setu.service.BankAccountVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/verify/bank account")
@RequiredArgsConstructor
public class BankAccountVerificationController {
    private final BankAccountVerificationService bankAccountVerificationService;

    @PostMapping
    public ResponseEntity<BankAccountResponseDto> submit(@ModelAttribute BankAccountRequestDto dto)
    {
        return ResponseEntity.ok(bankAccountVerificationService.submitBankAccount(dto));
    }
    @GetMapping("/status")
    public ResponseEntity<BankAccountResponseDto> getStatus() {
        return ResponseEntity.ok(bankAccountVerificationService.getStatus());
    }

    @PutMapping("/approve/{userId}")
    public ResponseEntity<BankAccountResponseDto> approve(@PathVariable Long userId) {
        return ResponseEntity.ok(bankAccountVerificationService.approveBank(userId));
    }
    @PutMapping("/reject/{userId}")
    public ResponseEntity<BankAccountResponseDto> reject(@PathVariable Long userId){
        return ResponseEntity.ok(bankAccountVerificationService.rejectBank(userId));
    }
}
