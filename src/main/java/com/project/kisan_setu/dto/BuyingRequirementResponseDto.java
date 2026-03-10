package com.project.kisan_setu.dto;

import com.project.kisan_setu.entity.DistrictMaster;
import com.project.kisan_setu.entity.StateMaster;
import com.project.kisan_setu.enums.Urgency;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyingRequirementResponseDto {

    private Long requirementId;

    private String cropName;
    private String variety;
    private String grade;

    private BigDecimal quantityRequired;
    private String unit;

    private BigDecimal expectedMinPrice;
    private BigDecimal expectedMaxPrice;

    private String state;
    private String district;
    private String deliveryAddress;

    private LocalDate deadline;

    private Urgency urgency;

    private String additionalNotes;

    private String buyerName;


}