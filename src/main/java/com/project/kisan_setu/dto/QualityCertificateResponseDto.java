package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class QualityCertificateResponseDto {

    private Long certificateId;
    private String certificateName;
    private LocalDate issuedDate;
    private String certificateUrl;
}

