package com.project.kisan_setu.dto.RequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudReportRequestDto {

    private Long reportedUserId;
    private Long fraudTypeId;
    private String description;
}
