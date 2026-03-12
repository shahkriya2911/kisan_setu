package com.project.kisan_setu.dto.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordResponseDto {
    private int status;
    private String message;
    private Long userId;
    private String fullName;
    private String email;
    private LocalDateTime passwordChangedAt;
}