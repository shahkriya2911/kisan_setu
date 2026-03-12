package com.project.kisan_setu.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MarketInsightDto {

    private Double avgWheatPrice;
    private Long liveAuctionsCount;
    private String topTradedCrop;
}
