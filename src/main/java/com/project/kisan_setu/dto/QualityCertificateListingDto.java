package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class QualityCertificateListingDto {
    private String certificateName;
    private LocalDate issuedDate;
    private MultipartFile file;
}
