package com.project.kisan_setu.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisputeDto {
    private Long disputeId;
    private Long orderId;  //TranscationId
    private String buyerName;
    private String sellerName;
    private String issueType;
    private String disputeStatus;
    private LocalDateTime createdDate;
}
