package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponseDto {
    private int status;
    private String message;
    private String accessToken;
    private String refreshToken;
    private UserResponseDto data;
}
