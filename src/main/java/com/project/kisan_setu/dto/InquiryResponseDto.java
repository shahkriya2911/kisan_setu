package com.project.kisan_setu.dto;

import com.project.kisan_setu.enums.InquiryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class InquiryResponseDto {

    private Long inquiryId;
    private Long listingId;
    private BigDecimal quantityRequested;
    private BigDecimal pricePerKg;
    private LocalDateTime inquiryTime;
    private BigDecimal remainingQuantity;
    private InquiryStatus status;
}
