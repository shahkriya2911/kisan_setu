package com.project.kisan_setu.dto.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentActivityDto {
    private String activityType;
    private String description;
    private Long sellerId;
    private Long buyerId;
    private LocalDateTime timestamp;
}
