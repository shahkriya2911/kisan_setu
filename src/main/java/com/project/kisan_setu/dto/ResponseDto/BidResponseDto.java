package com.project.kisan_setu.dto.ResponseDto;

import com.project.kisan_setu.enums.BidStatus;
import com.project.kisan_setu.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidResponseDto {

    //bid response info
    private Long bidId;
    private Long buyerId;
    private BigDecimal buyerAmount;
    private String buyerName;
    private LocalDateTime bidTime;
    private BidStatus bidStatus;
    private Long sellerId;
    private Long listingId;
}
