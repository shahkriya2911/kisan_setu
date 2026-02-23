package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by jackson
@AllArgsConstructor
public class QualityCertificateResponseDto {

    //response info
    private Long certificateId;
    private String certificateName;
    private LocalDate issuedDate;
    private String certificateUrl;
}

