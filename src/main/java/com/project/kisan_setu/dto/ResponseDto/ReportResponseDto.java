package com.project.kisan_setu.dto.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDto {
    private Long reportId;
    private String transactionId;
    private String buyerName;
    private String sellerName;
    private String issueType;
    private String status;
    private LocalDateTime createdAt;
}
