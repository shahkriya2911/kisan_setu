package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter //getters
@Setter //setters
@NoArgsConstructor
public class UpdateUserRequestDto {

    //update user info
    private String userFullName;
    private String email;
    private String userPhoneNumber;
    private String userPassword;


}
