package com.project.kisan_setu.dto.RequestDto;

import com.project.kisan_setu.enums.ReportReason;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportSellerRequestDto {
    @NotNull
    private ReportReason reason;
    private String description;
}
