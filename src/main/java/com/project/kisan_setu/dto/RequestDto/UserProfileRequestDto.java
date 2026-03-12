package com.project.kisan_setu.dto.RequestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequestDto {
    @NotBlank(message = "date of birth is required")
    private String dateOfBirth;
    @NotBlank(message = "bank name is required")
    private String bankName;
    @NotBlank(message = "account number is required")
    private String accountNumber;
    @NotBlank(message = "ifsc code is required")
    private String ifscCode;
    @NotBlank(message = "UPI ID is required")
    private String upiId;
    @NotNull(message = "farm size is required")
    private BigDecimal farmSize;
    @NotBlank(message = "primary crops is required")
    private String primaryCrops;
    @NotBlank(message = "farm location is required")
    private String farmLocation;
    @NotNull(message = "years of experience is required")
    private Integer yearsOfExperience;
}
