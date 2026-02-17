package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BuyingRequirementRequestDto {
    private String cropType;
    private String quantity;
    private Double minPrice;
    private Double maxPrice;
    private String qualityGrade;
    private String delieveryLocation;
    private LocalDateTime deadline;
    private Long buyerId; // only ID, no full object
}
