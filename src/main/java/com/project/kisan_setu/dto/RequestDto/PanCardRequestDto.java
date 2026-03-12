package com.project.kisan_setu.dto.RequestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PanCardRequestDto {

    @NotBlank(message = "PAN number is required")
    @Pattern(
            regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}",
            message = "Invalid PAN format! Example: ABCDE1234F"
    )
    private String panNumber;

    @NotBlank(message = "Name on PAN is required")
    private String nameOnPan;

    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;

    private MultipartFile panImage;
}