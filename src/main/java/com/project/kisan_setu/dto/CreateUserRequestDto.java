package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestDto {
    private String userFullName;
    private String userEmail;
    private String userPhoneNumber;
    private String userPassword;
    private String userCreatedAt;
    private String userAddress;
}
