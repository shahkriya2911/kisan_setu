package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.AadhaarRequestDto;
import com.project.kisan_setu.dto.AadhaarResponseDto;
import com.project.kisan_setu.entity.AadhaarVerification;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.repository.AadhaarVerificationRepository;
import com.project.kisan_setu.service.AadhaarVerificationService;
import com.project.kisan_setu.service.FileStorageService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
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

    @Override
    public AadhaarResponseDto submitAadhaar(AadhaarRequestDto dto) {
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);


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

        return buildResponse(verification, user,
                "Aadhaar submitted! Wait for approval.");

    }

    private AadhaarResponseDto buildResponse(AadhaarVerification v, User user, String message) {
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
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        AadhaarVerification verification = aadhaarVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No Aadhaar submitted yet"));
        return buildResponse(verification, user, verification.isVerified() ?
                "Aadhaar verified!" :
                "Verification pending admin approval");

    }

    @Override
    public AadhaarResponseDto approveAadhaar(Long userId) {
        User user = validatorMethods.validateUserById(userId);
        AadhaarVerification verification = aadhaarVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No Aadhaar found"));

        if (verification.isVerified()) {
            throw new RuntimeException("Aadhaar already verified!");
        }
        verification.setVerified(true);
        verification.setVerifiedAt(LocalDateTime.now());
        aadhaarVerificationRepository.save(verification);
        return buildResponse(verification, user, "Aadhaar approved successfully!");
    }

    @Override
    public AadhaarResponseDto rejectAadhaar(Long userId) {
        User user = validatorMethods.validateUserById(userId);
        AadhaarVerification verification = aadhaarVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No Aadhaar found"));

        verification.setVerified(false);
        verification.setVerifiedAt(null);
        verification.setAadhaarImagePath(null); // reset so user resubmits
        aadhaarVerificationRepository.save(verification);


        return buildResponse(verification, user, "Aadhaar rejected! Please resubmit the Detail.");

    }

    @Override
    public List<AadhaarResponseDto> getPendingVerifications() {
        List<AadhaarVerification> pending = aadhaarVerificationRepository.findByVerified(false);
        return pending.stream()
                .map(v -> buildResponse(v, v.getUser(), "Pending verification"))
                .toList();
    }
}
