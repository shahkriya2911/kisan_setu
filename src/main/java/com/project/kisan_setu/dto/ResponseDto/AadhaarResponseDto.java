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
public class AadhaarResponseDto {
    private Long userId;
    private String aadhaarNumber;
    private String nameOnAadhaar;
    private String dateOfBirth;
    private String address;
    private String aadhaarImagePath;
    private boolean verified;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
    private String message;

}
