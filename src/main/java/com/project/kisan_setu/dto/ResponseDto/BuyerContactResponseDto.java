package com.project.kisan_setu.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyerContactResponseDto {
    private Long requirementId;
    private String buyerName;
    private String mobileNumber;
    private String cropName;
    private BigDecimal quantityRequired;
    private String unit;
}
