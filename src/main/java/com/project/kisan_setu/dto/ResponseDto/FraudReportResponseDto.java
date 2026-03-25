package com.project.kisan_setu.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudReportResponseDto {
    private Long id;
    private Long reportedBy;
    private Long reportedUser;
    private String fraudType;
    private String description;
    private String status;
    private LocalDateTime createdAt;
}
