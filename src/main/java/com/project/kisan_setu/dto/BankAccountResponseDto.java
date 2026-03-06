package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountResponseDto {
    private Long userId;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String accountHolderName;
    private String upiId;
    private MultipartFile aadhaarNumber;
    private MultipartFile panCard;
    private String aadhaarPath;
    private String panCardPath;
    private boolean verified;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
    private String message;


}
