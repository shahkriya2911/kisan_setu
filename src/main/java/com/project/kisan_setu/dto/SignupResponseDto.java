package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by jackson
@AllArgsConstructor //constructor
public class SignupResponseDto {
    private String accessToken;
    private UserResponseDto userResponseDto;
}
