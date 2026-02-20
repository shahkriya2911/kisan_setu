package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BidResponseDto {

    private Long bidId;
    private Double bidAmount;
    private String bidderName;
    private LocalDateTime bidTime;
}
