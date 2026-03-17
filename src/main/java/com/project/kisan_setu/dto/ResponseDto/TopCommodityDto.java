package com.project.kisan_setu.dto.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopCommodityDto {

    private String commodityName;
    private Long totalTrades;

}