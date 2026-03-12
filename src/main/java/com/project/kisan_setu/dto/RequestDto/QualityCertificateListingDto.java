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
public class QualityCertificateListingDto {
    @NotBlank(message = "certificate name is required")
    private String certificateFileName;
    @NotNull(message = "certificate file is required")
    private MultipartFile certificateFile;
}
