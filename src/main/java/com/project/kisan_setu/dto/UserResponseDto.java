package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long userId;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String userCreatedAt;

}
