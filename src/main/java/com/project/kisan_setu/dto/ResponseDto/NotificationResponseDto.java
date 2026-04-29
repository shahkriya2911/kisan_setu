package com.project.kisan_setu.dto.ResponseDto;
import com.project.kisan_setu.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationResponseDto {

    private Long notificationId;

    private String message;

    private Boolean isRead;

    private NotificationStatus type;


    // extra info
    private Long listingId;
    private Long bidId;
    private Long orderId;

    private String cropName;
    private String variety;
    private BigDecimal bidAmount;
    private BigDecimal quantity;
    private String unit;

    private String sellerName;
    private BigDecimal pricePerKg;
    private Boolean actionCompleted;
}