package com.project.kisan_setu.dto.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordResponseDto {
    private int status;
    private String message;
    private Long userId;

}