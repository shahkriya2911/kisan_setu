package com.project.kisan_setu.dto.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportUserRequestDto {
    private Long orderId;
    @NotNull
    private String reason;
    private String description;
}
