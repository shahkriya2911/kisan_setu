package com.project.kisan_setu.dto;

import com.project.kisan_setu.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    //user response info
    private Long id;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String userCreatedAt;

    public UserResponseDto(User user) {
        this.id = user.getUserId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.mobileNumber = user.getMobileNumber();

    }
}

