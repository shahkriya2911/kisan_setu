package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDto {
    private String userFullName;
    private String email;
    private String userPhoneNumber;
    private String userPassword;


}
