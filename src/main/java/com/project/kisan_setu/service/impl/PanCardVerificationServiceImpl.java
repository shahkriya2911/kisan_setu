package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.RequestDto.PanCardRequestDto;
import com.project.kisan_setu.dto.ResponseDto.PanCardResponseDto;
import com.project.kisan_setu.entity.PanCardVerification;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.repository.PanCardVerificationRepository;
import com.project.kisan_setu.service.FileStorageService;
import com.project.kisan_setu.service.NotificationService;
import com.project.kisan_setu.service.PanCardVerificationService;
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
public class PanCardVerificationServiceImpl implements PanCardVerificationService {
    private final PanCardVerificationRepository panCardVerificationRepository;
    private final ValidatorMethods validatorMethods;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;
    private static final Logger logger = LoggerFactory.getLogger(PanCardVerificationServiceImpl.class);
    @Override
    public PanCardResponseDto submitPan(PanCardRequestDto dto) {
        logger.info("Validating user for PAN submission...");
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        logger.info("Checking if PAN already exists in DB...");
        Optional<PanCardVerification> existing =
                panCardVerificationRepository.findByUserUserId(userId);

        if (existing.isPresent() && existing.get().isVerified()) {
            PanCardVerification v = existing.get();
            return buildResponse(v, user, "PAN already verified!");
        }

        logger.info("Validating PAN number...");
        if (dto.getPanNumber() == null || !dto.getPanNumber().matches("[A-Z]{5}[0-9]{4}[A-Z]{1}")) {
            throw new IllegalArgumentException("Invalid PAN format! Example: ABCDE1234F");
        }

        if (dto.getPanImage() == null || dto.getPanImage().isEmpty()) {
            throw new IllegalArgumentException("PAN image is required");
        }
        String imagePath = fileStorageService.storeFile(dto.getPanImage(), "Pan");
        PanCardVerification verification = existing.orElse(new PanCardVerification());
        verification.setUser(user);
        verification.setPanNumber(dto.getPanNumber());
        verification.setNameOnPan(dto.getNameOnPan());
        verification.setDateOfBirth(dto.getDateOfBirth());
        verification.setPanImagePath(imagePath);
        verification.setVerified(false);
        panCardVerificationRepository.save(verification);

        logger.info("PAN submitted successfully...");
        return buildResponse(verification, user, "PAN submitted! Wait for approval.");
    }

    @Override
    public PanCardResponseDto getPanStatus() {
        logger.info("Fetching PAN status...");
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        PanCardVerification verification = panCardVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No PAN submitted yet"));

        return buildResponse(verification, user, verification.isVerified()
                ? "PAN verified!"
                : "Verification pending admin approval");
    }

    @Override
    public PanCardResponseDto approvePan(Long userId) {
        validatorMethods.validateAdminAccess();
        logger.info("Approving PAN for userId: {}...", userId);
        User user = validatorMethods.validateUserById(userId);

        PanCardVerification verification = panCardVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No PAN found"));

        if (verification.isVerified()) {
            throw new IllegalStateException("PAN already verified!");
        }

        verification.setVerified(true);
        verification.setVerifiedAt(LocalDateTime.now());
        panCardVerificationRepository.save(verification);

        logger.info("PAN approved successfully for userId: {}...", userId);
        return buildResponse(verification, user, "PAN approved successfully!");

    }

    @Override
    public PanCardResponseDto rejectPan(Long userId) {
        logger.info("Rejecting PAN for userId: {}...", userId);
        User user = validatorMethods.validateUserById(userId);

        PanCardVerification verification = panCardVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No PAN found"));

        verification.setVerified(false);
        verification.setVerifiedAt(null);
        verification.setPanImagePath(null);
        panCardVerificationRepository.save(verification);

        logger.info("PAN rejected successfully for userId: {}...", userId);
        return buildResponse(verification, user, "PAN rejected! Please resubmit.");
    }

    @Override
    public List<PanCardResponseDto> getPendingVerifications() {
        validatorMethods.validateAdminAccess();
        logger.info("Fetching pending PAN verifications...");
        List<PanCardVerification> pending = panCardVerificationRepository.findByVerified(false);
        logger.info("Pending PAN verifications fetched successfully...");
        return pending.stream()
                .map(v -> buildResponse(v, v.getUser(), "Pending verification"))
                .toList();
    }

    private PanCardResponseDto buildResponse(PanCardVerification v, User user, String message) {
        logger.info("Building PAN response...");
        return PanCardResponseDto.builder()
                .userId(user.getUserId())
                .panNumber(v.getPanNumber())
                .nameOnPan(v.getNameOnPan())
                .dateOfBirth(v.getDateOfBirth())
                .panImagePath(v.getPanImagePath())
                .verified(v.isVerified() ? true : null)
                .submittedAt(v.getSubmittedAt())
                .verifiedAt(v.getVerifiedAt())
                .message(message)
                .build();
    }
}
