package com.project.kisan_setu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter //getters
@Setter //setters
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageListingDto {
    //image info send from frontend
    @NotNull
    @Positive
    private Long listingId;
    @NotNull(message = "image file is required")
    private MultipartFile file;
    @NotNull
    private Boolean isPrimary;
}

