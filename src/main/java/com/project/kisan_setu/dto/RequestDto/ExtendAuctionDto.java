package com.project.kisan_setu.dto.RequestDto;

import lombok.Data;

@Data
public class ExtendAuctionDto {
    private Long listingId;
    private Long sellerId;
    private Integer minutes;
}