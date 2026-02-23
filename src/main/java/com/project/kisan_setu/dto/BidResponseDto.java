package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by jackson
@AllArgsConstructor //constructor
public class BidResponseDto {

    //bid response info
    private Long bidId;
    private Double bidAmount;
    private String bidderName;
    private LocalDateTime bidTime;
}
