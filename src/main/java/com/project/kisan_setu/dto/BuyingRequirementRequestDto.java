package com.project.kisan_setu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class BuyingRequirementRequestDto {

    //buying requirement info
    @NotBlank(message = "crop type is required")
    private String cropType;
    @NotNull(message = "required quantity is needed")
    private Double quantityRequired;
    @NotBlank(message = "unit is required")
    private String unit;
    @NotNull(message = "minimum price is required")
    private Double minPrice;
    @NotNull(message = "maximum price is required")
    private Double maxPrice;
    @NotBlank(message = "quality grade is required")
    private String qualityGrade;
    @NotBlank(message = "delivery location is required")
    private String deliveryLocation;
    @NotBlank(message = "deadline is required")
    private LocalDate deadline;
    private String additionalNotes;
}