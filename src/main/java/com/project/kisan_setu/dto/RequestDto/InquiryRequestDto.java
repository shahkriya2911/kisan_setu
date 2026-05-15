package com.project.kisan_setu.dto.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryRequestDto {

    private Long listingId;
    @NotNull(message = "quantity requested required")
    private BigDecimal quantityRequested;
}