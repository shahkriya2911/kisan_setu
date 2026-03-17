package com.project.kisan_setu.dto.ResponseDto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EscrowGrowthDto {
    private String month;
    private Double amount;

}