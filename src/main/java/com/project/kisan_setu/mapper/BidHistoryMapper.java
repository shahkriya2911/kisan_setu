package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.BidHistoryDto;
import com.project.kisan_setu.entity.BidHistory;

public class BidHistoryMapper {

    public static BidHistoryDto toDto(BidHistory bid) {

        BidHistoryDto dto = new BidHistoryDto();
        dto.setBuyerName(String.valueOf(bid.getBuyer()));
        dto.setBidAmountPerKg(bid.getAmountPerKg());
        dto.setTotalBidAmount(bid.getAmountPerKg());
        dto.setBidTime(bid.getBidTime());

        return dto;

    }
}

