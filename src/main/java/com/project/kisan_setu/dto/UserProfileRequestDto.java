package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequestDto {
    private String dateOfBirth;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String upiId;
    private BigDecimal farmSize;
    private String primaryCrops;
    private String farmLocation;
    private Integer yearsOfExperience;
}
