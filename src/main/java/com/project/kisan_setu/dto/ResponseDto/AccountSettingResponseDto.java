package com.project.kisan_setu.dto.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountSettingResponseDto {
    private Long userId;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String profilePhotoUrl;
    private String farmLocation;

}