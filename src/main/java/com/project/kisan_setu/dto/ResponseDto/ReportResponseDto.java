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
public class ReportResponseDto {
    private Long reportId;
    private String buyerName;
    private String sellerName;
    private String issueType;
    private String status;
    private boolean isBuyerReported;
    private boolean isSellerReported;
    private LocalDateTime createdAt;


}
