package com.project.kisan_setu.dto.ResponseDto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyTransactionDto {

    private String day;
    private Long transactions;

}