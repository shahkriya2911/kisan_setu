package com.project.kisan_setu.service;
import com.project.kisan_setu.dto.ResponseDto.AuctionInfoDto;

public interface AuctionService {
    AuctionInfoDto getAuctionInfo(Long listingId);
}
