package com.project.kisan_setu.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryResponseDto {
    private Long orderId;
    private Long listingId;
    private String commodity;
    private String buyerName;
    private String sellerName;
    private BigDecimal quantity;
    private BigDecimal amount;
    private String type;
    private String paymentLabel;
    private String orderStatus;
    private LocalDateTime orderedAt;
}
