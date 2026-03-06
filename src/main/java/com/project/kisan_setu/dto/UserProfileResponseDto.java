package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponseDto {
        private Long userId;
        private String fullName;
        private String email;
        private String mobileNumber;
        private String dateOfBirth;
        private String bankName;
        private String accountNumber;
        private String ifscCode;
        private String upiId;
        private BigDecimal farmSize;
        private String primaryCrops;
        private String farmLocation;
        private Integer yearsOfExperience;
        private boolean mobileVerified;
        private boolean aadhaarVerified;
        private boolean bankAccountVerified;
        private LocalDateTime mobileVerifiedAt;
        private LocalDateTime aadhaarVerifiedAt;
        private LocalDateTime bankAccountVerifiedAt;

}

