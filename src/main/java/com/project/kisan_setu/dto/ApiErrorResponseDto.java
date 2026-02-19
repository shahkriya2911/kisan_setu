package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponseDto {

    private int status;
    private Object data;
    private String path;
    private LocalDateTime timestamp;
    private String error;
}
