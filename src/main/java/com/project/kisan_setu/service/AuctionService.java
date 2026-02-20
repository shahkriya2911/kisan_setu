package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.AuctionInfoDto;

public interface AuctionService {
    AuctionInfoDto getAuctionInfo(Long listingId);
}
