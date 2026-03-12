package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.RequestDto.BankAccountRequestDto;
import com.project.kisan_setu.dto.ResponseDto.BankAccountResponseDto;
import com.project.kisan_setu.entity.BankAccountVerification;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.repository.BankAccountVerificationRepository;
import com.project.kisan_setu.service.BankAccountVerificationService;
import com.project.kisan_setu.service.FileStorageService;
import com.project.kisan_setu.service.NotificationService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final NotificationService notificationService;
    private static final Logger logger = LoggerFactory.getLogger(BankAccountVerificationServiceImpl.class);
    @Override
    public BankAccountResponseDto submitBankAccount(BankAccountRequestDto dto) {
        logger.info("Validating user for submitting bank account...");
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        logger.info("Checking if bank account exists in DB or not...");
        Optional<BankAccountVerification> existing =
                bankAccountVerificationRepository.findByUserUserId(userId);

        if (existing.isPresent() && existing.get().isVerified()) {
            return buildResponse(existing.get(), user,
                    "Bank account already verified!");
        }

        logger.info("Validating bank account...");
        if (dto.getAccountNumber() == null || dto.getAccountNumber().isEmpty()) {
            throw new RuntimeException("Account number is required");
        }
        if (dto.getIfscCode() == null || !dto.getIfscCode().matches("[A-Z]{4}0[A-Z0-9]{6}")) {
            throw new RuntimeException("Invalid IFSC code! Format: SBIN0001234");
        }
        if(dto.getAadhaarCard() == null || dto.getAadhaarCard().isEmpty()){
            throw new UserException("Bank Document Aadhaar Required");
        }
        if(dto.getPanCard() == null || dto.getPanCard().isEmpty()){
            throw new UserException("Bank Document Pan card required ");
        }
        String aadhaar = fileStorageService.storeFile(
                dto.getAadhaarCard(), "aadhaar");

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

        logger.info("Bank account details submitted success...");
        return buildResponse(verification, user,
                "Bank details submitted! Pending admin approval.");

    }

    @Override
    public BankAccountResponseDto getStatus() {
        logger.info("Validating user to get bank account status...");
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        logger.info("Checking if bank account details exists in DB or not...");
        BankAccountVerification verification = bankAccountVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No bank details submitted yet"));

        logger.info("Bank account verified success...");
        return buildResponse(verification, user,
                verification.isVerified() ? "Bank account verified!" : "Verification pending admin approval");
    }

    @Override
    public BankAccountResponseDto approveBank(Long userId) {
        validatorMethods.validateAdminAccess();
        logger.info("Validating user to for bank account approval");
        User user = validatorMethods.validateUserById(userId);

        logger.info("Checking if bank account details exits in DB and is bank account verified or not...");
        BankAccountVerification verification = bankAccountVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No bank details found"));

        if (verification.isVerified()) {
            throw new RuntimeException("Bank account already verified!");
        }

        verification.setVerified(true);
        verification.setVerifiedAt(LocalDateTime.now());
        bankAccountVerificationRepository.save(verification);
        notificationService.notifyUser(user, "Your bank account has been verified and linked successfully!");
        logger.info("Bank account approval success...");
        return buildResponse(verification, user, "Bank account approved successfully!");
    }
    @Override
    public BankAccountResponseDto rejectBank(Long userId) {
        validatorMethods.validateAdminAccess();
        logger.info("Validating user to reject bank account details...");
        User user = validatorMethods.validateUserById(userId);

        logger.info("Checking if bank account exists in DB and is verified...");
        BankAccountVerification verification = bankAccountVerificationRepository
                .findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "No bank details found"));

        verification.setVerified(false);
        verification.setVerifiedAt(null);
        verification.setAadhaarPath(null);
        verification.setPanCardPath(null);
        bankAccountVerificationRepository.save(verification);
        notificationService.notifyUser(user, "Your bank account has been verified and linked Rejected!");

        logger.info("Bank account rejection success");
        return buildResponse(verification, user,
                "Bank account rejected! Please resubmit.");
    }

    @Override
    public List<BankAccountResponseDto> getPendingVerifications() {
        logger.info("Getting pending verifications...");
        List<BankAccountVerification> pending =
                bankAccountVerificationRepository.findByVerified(false);
        logger.info("Fetching pending verifications success...");
        return pending.stream().map(v -> buildResponse(v, v.getUser(),
                        "Pending verification"))
                .toList();
    }

    private BankAccountResponseDto buildResponse(BankAccountVerification v, User user, String message) {
        logger.info("Building bank account details response...");
        logger.info("Bank account details response generated...");
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
