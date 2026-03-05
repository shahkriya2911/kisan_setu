package com.project.kisan_setu.dto;

import com.project.kisan_setu.enums.Urgency;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyingRequirementRequestDto {

    @NotBlank(message = "Crop name is required")
    private String cropName;

    @NotBlank(message = "Variety is required")
    private String variety;

    @NotBlank(message = "Grade is required")
    private String grade;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private BigDecimal quantityRequired;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Minimum expected price is required")
    @Positive(message = "Minimum price must be greater than 0")
    private BigDecimal minPrice;

    @NotNull(message = "Maximum expected price is required")
    @Positive(message = "Maximum price must be greater than 0")
    private BigDecimal maxPrice;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private LocalDate deadline;

    @NotNull(message = "Urgency is required")
    private Urgency urgency;

    private String additionalNotes;

}