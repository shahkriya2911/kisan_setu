package com.project.kisan_setu.dto.RequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AadhaarRequestDto {
    @NotBlank(message = "Aadhar number cannot be empty")
    private String aadhaarNumber;
    @NotBlank(message = "Name on aadhaar cannot be empty")
    private String nameOnAadhaar;
    @NotBlank(message = "Date of birth cannot be empty")
    private String dateOfBirth;
    @NotBlank(message = "Address cannot be empty")
    private String address;
    @NotNull(message = "Aadhaar image cannot be empty")
    private MultipartFile aadhaarImage;
}
