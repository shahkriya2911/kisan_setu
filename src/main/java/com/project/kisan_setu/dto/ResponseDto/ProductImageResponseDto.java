package com.project.kisan_setu.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageResponseDto {

    private String fileName;
    private String filePath;
    private String fileType;
    private Boolean isPrimary;


}