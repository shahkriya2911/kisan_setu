package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.BankAccountRequestDto;
import com.project.kisan_setu.dto.BankAccountResponseDto;
import com.project.kisan_setu.entity.BankAccountVerification;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.repository.BankAccountVerificationRepository;
import com.project.kisan_setu.service.BankAccountVerificationService;
import com.project.kisan_setu.service.FileStorageService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankAccountVerificationServiceImpl implements BankAccountVerificationService {
    private final BankAccountVerificationRepository bankAccountVerificationRepository;
    private final ValidatorMethods validatorMethods;
    private final FileStorageService fileStorageService;
    @Override
    public BankAccountResponseDto submitBankAccount(BankAccountRequestDto dto) {
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);
        Optional<BankAccountVerification> existing =
                bankAccountVerificationRepository.findByUserUserId(userId);

        if (existing.isPresent() && existing.get().isVerified()) {
            return buildResponse(existing.get(), user,
                    "Bank account already verified!");
        }
        if (dto.getAccountNumber() == null || dto.getAccountNumber().isEmpty()) {
            throw new RuntimeException("Account number is required");
        }
        if (dto.getIfscCode() == null || !dto.getIfscCode().matches("[A-Z]{4}0[A-Z0-9]{6}")) {
            throw new RuntimeException("Invalid IFSC code! Format: SBIN0001234");
        }
        if(dto.getAadhaarNumber() == null || dto.getAadhaarNumber().isEmpty()){
            throw new UserException("Bank Document Aadhaar Required");
        }
        if(dto.getPanCard() == null || dto.getPanCard().isEmpty()){
            throw new UserException("Bank Document Pan card required ");
        }
        String aadhaar = fileStorageService.storeFile(
                dto.getAadhaarNumber(), "aadhaar");

        String pan = fileStorageService.storeFile(
                dto.getPanCard(), "pan card");BankAccountVerification verification =
                existing.orElse(new BankAccountVerification());
        verification.setUser(user);
        verification.setBankName(dto.getBankName());
        verification.setAccountNumber(dto.getAccountNumber());
        verification.setIfscCode(dto.getIfscCode());
        verification.setAccountHolderName(dto.getAccountHolderName());
        verification.setUpiId(dto.getUpiId());
        verification.setAadhaarPath(aadhaar);
        verification.setPanCardPath(pan);
        verification.setVerified(false);
        bankAccountVerificationRepository.save(verification);

        return buildResponse(verification, user,
                "Bank details submitted! Pending admin approval.");

    }

    @Override
    public BankAccountResponseDto getStatus() {
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        BankAccountVerification verification = bankAccountVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No bank details submitted yet"));

        return buildResponse(verification, user,
                verification.isVerified() ? "Bank account verified!" : "Verification pending admin approval");
    }

    @Override
    public BankAccountResponseDto approveBank(Long userId) {
        User user = validatorMethods.validateUserById(userId);

        BankAccountVerification verification = bankAccountVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No bank details found"));

        if (verification.isVerified()) {
            throw new RuntimeException("Bank account already verified!");
        }

        verification.setVerified(true);
        verification.setVerifiedAt(LocalDateTime.now());
        bankAccountVerificationRepository.save(verification);

        return buildResponse(verification, user, "Bank account approved successfully!");
    }
    @Override
    public BankAccountResponseDto rejectBank(Long userId) {
        User user = validatorMethods.validateUserById(userId);

        BankAccountVerification verification = bankAccountVerificationRepository
                .findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "No bank details found"));

        verification.setVerified(false);
        verification.setVerifiedAt(null);
        verification.setAadhaarPath(null);
        verification.setPanCardPath(null);
        bankAccountVerificationRepository.save(verification);

        return buildResponse(verification, user,
                "Bank account rejected! Please resubmit.");
    }

    @Override
    public List<BankAccountResponseDto> getPendingVerifications() {
        List<BankAccountVerification> pending =
                bankAccountVerificationRepository.findByVerified(false);

        return pending.stream().map(v -> buildResponse(v, v.getUser(),
                        "Pending verification"))
                .toList();
    }

    private BankAccountResponseDto buildResponse(BankAccountVerification v, User user, String message) {
        return BankAccountResponseDto .builder()
                .userId(user.getUserId())
                .bankName(v.getBankName())
                .accountNumber(v.getAccountNumber())
                .ifscCode(v.getIfscCode())
                .accountHolderName(v.getAccountHolderName())
                .upiId(v.getUpiId())
                .aadhaarPath(v.getAadhaarPath())
                .panCardPath(v.getPanCardPath())
                .verified(v.isVerified())
                .submittedAt(v.getSubmittedAt())
                .verifiedAt(v.getVerifiedAt())
                .message(message)
                .build();
    }
}
