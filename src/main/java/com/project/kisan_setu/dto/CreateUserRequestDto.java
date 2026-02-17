package com.project.kisan_setu.dto;

import com.project.kisan_setu.enums.UserRoles;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateUserRequestDto {
    private String userFullName;
    private String userEmail;
    private String userPhoneNumber;
    private String userPassword;
    private String userCreatedAt;
    private String userAddress;
    private List<UserRoles> roles;
}
