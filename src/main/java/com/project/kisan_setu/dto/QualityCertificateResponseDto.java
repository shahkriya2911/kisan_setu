package com.project.kisan_setu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class QualityCertificateResponseDto {

    private String fileName;
    private String filePath;
    private String fileType;
}