package com.project.kisan_setu.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDto {

    @NotBlank(message = "Full name is required")
    @Size(min = 5, max = 20, message = "Full name must be between 5 and 20 characters")
    @Pattern(
            regexp = "^[A-Za-z]+( [A-Za-z]+){0,2}$",
            message = "Only alphabets allowed, maximum two spaces"
    )
    private String fullName;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "Mobile number must start with 6-9 and contain 10 digits"
    )
    private String mobileNumber;
    @NotBlank(message = "Password is required")
//    @Size(min = 6, message = "Password must be at least 6 characters")
//    @Pattern(
//            regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,}$",
//            message = "Password must contain at least one digit and one special character"
//    )
    private String password;
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    private String userCreatedAt; // ISO_LOCAL_DATE_TIME format
}
