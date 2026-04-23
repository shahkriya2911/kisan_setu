package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.RequestDto.MobileOtpVerificationDto;
import com.project.kisan_setu.dto.ResponseDto.MobileOtpVerificationResponseDto;
import com.project.kisan_setu.entity.MobileVerification;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.repository.MobileVerificationRepository;
import com.project.kisan_setu.service.EmailService;
import com.project.kisan_setu.service.MobileVerificationService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MobileVerificationServiceImpl implements MobileVerificationService {
    private final ValidatorMethods validatorMethods;
    private final MobileVerificationRepository mobileVerificationRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(MobileVerificationServiceImpl.class);
    @Override
    public MobileOtpVerificationResponseDto sendOtp() {
        logger.info("Validating user to send otp...");
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);
        //check verified or not
        logger.info("Checking is mobile is verified or not...");
        Optional<MobileVerification> existing =mobileVerificationRepository.findByUserUserId(userId);

        if(existing.isPresent() && existing.get().isVerified())
        {
            MobileVerification m = existing.get();
            return MobileOtpVerificationResponseDto.builder()
                    .userId(user.getUserId())
                    .mobileNumber(m.getMobileNumber())
                    .email(user.getEmail())
                    .verified(true)
                    .otpSentAt(m.getOtpSentAt())
                    .verifiedAt(m.getVerifiedAt())
                    .message("Mobile Number Already Exist")
                    .build();

        }
        String otp = String.valueOf((int)(Math.random() * 9000)+100000);

        MobileVerification verification = existing.orElse(new MobileVerification());
        verification.setUser(user);
        verification.setMobileNumber(user.getMobileNumber());
        verification.setOtp(otp);
        verification.setOtpSentAt(LocalDateTime.now());
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        verification.setVerified(false);
        mobileVerificationRepository.save(verification);

        emailService.sendOtpEmail(user.getEmail(),otp);
        logger.info("Mobile otp sent success...");
        return MobileOtpVerificationResponseDto.builder()
                .userId(user.getUserId())
                .mobileNumber(user.getMobileNumber())
                .email(user.getEmail())
                .verified(false)
                .otpSentAt(verification.getOtpSentAt())
                .verifiedAt(null)
                .message("OTP sent to " + user.getEmail())
                .build();
    }


    @Override
    public MobileOtpVerificationResponseDto verifyOtp(MobileOtpVerificationDto dto) {
        logger.info("Validating user to verify otp...");
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);

        logger.info("Checking is otp is sent or not...");
        MobileVerification verification = mobileVerificationRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Please send OTP first"));

        if (verification.isVerified()) {
            return MobileOtpVerificationResponseDto.builder()
                    .userId(user.getUserId())
                    .mobileNumber(verification.getMobileNumber())
                    .email(user.getEmail())
                    .verified(true)
                    .otpSentAt(verification.getOtpSentAt())
                    .verifiedAt(verification.getVerifiedAt())
                    .message("Mobile already verified!")
                    .build();
        }
        if (LocalDateTime.now().isAfter(verification.getExpiresAt())) {
            throw new RuntimeException("OTP expired! Please request a new one");
        }
        if (!verification.getOtp().equals(dto.getOtp())) {
            throw new RuntimeException("Invalid OTP!");
        }
        verification.setVerified(true);
        verification.setVerifiedAt(LocalDateTime.now());
        mobileVerificationRepository.save(verification);

        logger.info("Mobile verified success...");
        return MobileOtpVerificationResponseDto.builder()
                .userId(user.getUserId())
                .mobileNumber(verification.getMobileNumber())
                .email(user.getEmail())
                .verified(true)
                .otpSentAt(verification.getOtpSentAt())
                .verifiedAt(verification.getVerifiedAt())
                .message("Mobile verified successfully!")
                .build();

    }

}
