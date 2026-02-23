package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by jackson
@AllArgsConstructor //constructor
public class BuyingRequirementRequestDto {

    //buying requirement info
    private String cropType;
    private Double quantityRequired;
    private Double minPrice;
    private Double maxPrice;
    private String qualityGrade;
    private String deliveryLocation;
    private LocalDate deadline;
    private String additionalNotes;
}