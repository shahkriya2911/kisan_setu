package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.RequestDto.AadhaarRequestDto;
import com.project.kisan_setu.dto.ResponseDto.AadhaarResponseDto;
import com.project.kisan_setu.entity.AadhaarVerification;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.repository.AadhaarVerificationRepository;
import com.project.kisan_setu.service.AadhaarVerificationService;
import com.project.kisan_setu.service.FileStorageService;
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
public class AadhaarVerificationServiceImpl implements AadhaarVerificationService {
    private final AadhaarVerificationRepository aadhaarVerificationRepository;
    private final ValidatorMethods validatorMethods;
    private final FileStorageService fileStorageService;
    private final static Logger logger = LoggerFactory.getLogger(AadhaarVerificationServiceImpl.class);

    @Override
    public AadhaarResponseDto submitAadhaar(AadhaarRequestDto dto) {
        logger.info("Validating user for submit aadhaar request...");
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        logger.info("Checking if aadhaar already exists for user in DB or not...");
        Optional<AadhaarVerification> existing =
                aadhaarVerificationRepository.findByUserUserId(userId);

        if (existing.isPresent() && existing.get().isVerified()) {
            AadhaarVerification v = existing.get();
            return AadhaarResponseDto.builder()
                    .userId(user.getUserId())
                    .aadhaarNumber(v.getAadhaarNumber())
                    .nameOnAadhaar(v.getNameOnAadhaar())
                    .dateOfBirth(v.getDateOfBirth())
                    .address(v.getAddress())
                    .aadhaarImagePath(v.getAadhaarImagePath())
                    .verified(v.isVerified())
                    .submittedAt(v.getSubmittedAt())
                    .verifiedAt(v.getVerifiedAt())
                    .message("Aadhaar already verified!")
                    .build();
        }

        logger.info("Checking validity of aadhaar...");
        if (dto.getAadhaarNumber() == null || !dto.getAadhaarNumber().matches("\\d{12}")) {
            throw new RuntimeException("Invalid Aadhaar! Must be 12 digits");
        }

        if (dto.getAadhaarImage() == null || dto.getAadhaarImage().isEmpty()) {
            throw new RuntimeException("Aadhaar image is required");
        }
        String imagePath = fileStorageService.storeFile(dto.getAadhaarImage(), "aadhaar");
        AadhaarVerification verification = existing.orElse(new AadhaarVerification());
        verification.setUser(user);
        verification.setAadhaarNumber(dto.getAadhaarNumber());
        verification.setNameOnAadhaar(dto.getNameOnAadhaar());
        verification.setDateOfBirth(dto.getDateOfBirth());
        verification.setAddress(dto.getAddress());
        verification.setAadhaarImagePath(imagePath);
        verification.setVerified(false);
        aadhaarVerificationRepository.save(verification);

        logger.info("Aadhaar submitted success...");
        return buildResponse(verification, user,
                "Aadhaar submitted! Wait for approval.");
    }

    private AadhaarResponseDto buildResponse(AadhaarVerification v, User user, String message) {
        logger.info("Building response for aadhaar...");
        logger.info("Aadhaar response generated");
        return AadhaarResponseDto.builder()
                .userId(user.getUserId())
                .aadhaarNumber(v.getAadhaarNumber())
                .nameOnAadhaar(v.getNameOnAadhaar())
                .dateOfBirth(v.getDateOfBirth())
                .address(v.getAddress())
                .aadhaarImagePath(v.getAadhaarImagePath())
                .verified(v.isVerified())
                .submittedAt(v.getSubmittedAt())
                .verifiedAt(v.getVerifiedAt())
                .message(message)
                .build();
    }

    @Override
    public AadhaarResponseDto getAadhaarStatus() {
        logger.info("Validating user for getting aadhaar status...");
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        logger.info("Verifying aadhaar...");
        AadhaarVerification verification = aadhaarVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No Aadhaar submitted yet"));
        logger.info("Aadhaar verified success...");
        return buildResponse(verification, user, verification.isVerified() ?
                "Aadhaar verified!" :
                "Verification pending admin approval");
    }

    @Override
    public AadhaarResponseDto approveAadhaar(Long userId) {
        validatorMethods.validateAdminAccess();
        logger.info("Validating user for aadhaar approval...");
        User user = validatorMethods.validateUserById(userId);

        AadhaarVerification verification = aadhaarVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No Aadhaar found"));

        if (verification.isVerified()) {
            throw new RuntimeException("Aadhaar already verified!");
        }
        verification.setVerified(true);
        verification.setVerifiedAt(LocalDateTime.now());
        aadhaarVerificationRepository.save(verification);
//        notificationService.notifyUser(user, " Your Aadhaar card has been verified successfully!");
        logger.info("aadhaar approved successfully");
        return buildResponse(verification, user, "Aadhaar approved successfully!");
    }
    @Override
    public AadhaarResponseDto rejectAadhaar(Long userId) {
        validatorMethods.validateAdminAccess();
        logger.info("validating user for rejecting aadhaar...");
        User user = validatorMethods.validateUserById(userId);
        AadhaarVerification verification = aadhaarVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No Aadhaar found"));

        verification.setVerified(false);
        verification.setVerifiedAt(null);
        verification.setAadhaarImagePath(null); // reset so user resubmits
        aadhaarVerificationRepository.save(verification);
//        notificationService.notifyUser(user, "Your Aadhaar card has been Rejected...!");

        logger.info("aadhaar reject success...");
        return buildResponse(verification, user, "Aadhaar rejected! Please resubmit the Detail.");

    }
    @Override
    public List<AadhaarResponseDto> getPendingVerifications() {
        validatorMethods.validateAdminAccess();
        logger.info("getting pending verifications...");
        List<AadhaarVerification> pending = aadhaarVerificationRepository.findByVerified(false);
        logger.info("fetching pending verifications success...");
        return pending.stream()
                .map(v -> buildResponse(v, v.getUser(), "Pending verification"))
                .toList();
    }
}
