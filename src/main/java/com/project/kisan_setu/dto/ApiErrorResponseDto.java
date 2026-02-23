package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter //getters
@Setter //setters
@AllArgsConstructor //constructor
@NoArgsConstructor //needed by jackson
public class ApiErrorResponseDto {

    //common error api response
    private int status;
    private Object data;
    private String path;
    private LocalDateTime timestamp;
    private String error;
}
