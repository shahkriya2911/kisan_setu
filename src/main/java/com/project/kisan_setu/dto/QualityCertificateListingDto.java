package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by jackson
public class QualityCertificateListingDto {
    private Long listingId;
    //certificate info
    private String certificateName;
    private LocalDate issuedDate;
    private MultipartFile file;
}
