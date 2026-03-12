package com.project.kisan_setu.dto.ResponseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductImageResponseDto {

    private String fileName;
    private String filePath;
    private String fileType;
    private Boolean isPrimary;
}