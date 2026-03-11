package com.project.kisan_setu.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequestDto {
    @NotBlank(message = "Current password is required")
    private String currentPassword;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,}$",
            message = "Password must contain at least one digit and one special character"
    )
    private String newPassword;
    @NotBlank(message = "Confirm password is required")
    private String confirmNewPassword;
}
