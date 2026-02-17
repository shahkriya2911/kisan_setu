package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuyingRequirementResponseDto {
    private Long id;
    private String cropType;
    private String quantity;
    private Double minPrice;
    private Double maxPrice;
    private String qualityGrade;
    private String delieveryLocation;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long buyerId; // only ID for frontend
    private String buyerName; // optional
}
