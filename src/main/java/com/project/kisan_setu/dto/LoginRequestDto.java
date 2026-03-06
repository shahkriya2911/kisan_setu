package com.project.kisan_setu.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequestDto {

    //login info
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
//    @NotBlank(message = "Password is required")
//    @Size(min = 6, message = "Password must be at least 6 characters")
//    @Pattern(
//            regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,}$",
//            message = "Password must contain at least one digit and one special character"
//    )
    private String password;
}
