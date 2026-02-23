package com.project.kisan_setu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by jackson
@AllArgsConstructor
public class QualityCertificateListingDto {
    @NotNull
    @Positive
    private Long listingId;
    //certificate info
    @NotBlank(message = "certificate name is required")
    private String certificateName;
    @NotNull(message = "issued date is required")
    private LocalDate issuedDate;
    @NotNull(message = "certificate file is required")
    private MultipartFile file;
}
