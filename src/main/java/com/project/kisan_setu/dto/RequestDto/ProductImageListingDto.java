package com.project.kisan_setu.dto.RequestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageListingDto {
    //image info send from frontend
    @NotBlank(message = "image file name is required")
    private String imageFileName;
    @NotBlank(message = "image file is required")
    private MultipartFile imageFile;
    @NotNull
    private Boolean isPrimary;
}

