package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyingRequirementResponseDto {

    //buying requirement response info
    private Long id;
    private String cropType;
    private Double quantityRequired;
    private String deliveryLocation;
    private LocalDate deadline;
}
