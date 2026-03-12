package com.project.kisan_setu.dto.ResponseDto;
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
public class RecentBidResponseDto {
    private Long bidderId;
    private String bidderName;
    private String cropListing;
    private BigDecimal bidAmount;
    private LocalDateTime timestamp;
    private String bidStatus;


}
