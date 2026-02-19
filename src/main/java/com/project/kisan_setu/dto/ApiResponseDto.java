package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponseDto<T> {

    private int status;
    private String message;
    private String accessToken;  // token at top-level
    private T data;              // user info or other response
}


