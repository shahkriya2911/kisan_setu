package com.project.kisan_setu.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AadhaarRequestDto {
    private String aadhaarNumber;
    private String nameOnAadhaar;
    private String dateOfBirth;
    private String address;
    private MultipartFile aadhaarImage;
}
