package com.project.kisan_setu.dto;

import com.project.kisan_setu.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by jackson
@AllArgsConstructor //constructor
public class LoginResponseDto {
    private int status;
    private String message;
    private String accessToken;
    private UserResponseDto data;
}
