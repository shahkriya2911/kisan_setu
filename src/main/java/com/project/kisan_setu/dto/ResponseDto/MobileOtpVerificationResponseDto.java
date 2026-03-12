package com.project.kisan_setu.dto.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MobileOtpVerificationResponseDto {

    private Long userId;
    private String mobileNumber;
    private String email;
    private boolean verified;
    private LocalDateTime otpSentAt;
    private LocalDateTime verifiedAt;
    private String message;
}
