package com.project.kisan_setu.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuyerRequirementSummaryDto {

    private Long totalRequirements;
    private Long normalRequirements;
    private Long urgentRequirements;
}
