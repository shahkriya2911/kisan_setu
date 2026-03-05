package com.project.kisan_setu.dto;

import com.project.kisan_setu.enums.InquiryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InquiryResponseDto {

    private Long inquiryId;
    private Long listingId;
    private String buyerName;
    private String cropName;
    private BigDecimal quantityRequested;
    private LocalDateTime inquiryTime;
    private InquiryStatus status;
    private BigDecimal remainingQuantity;



}
