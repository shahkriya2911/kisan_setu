package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.MarketFilterRequestDto;
import com.project.kisan_setu.dto.MarketInsightDto;
import com.project.kisan_setu.dto.MarketListingResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketService {

    Page<MarketListingResponseDto> getLiveListings(
            MarketFilterRequestDto filter,
            Pageable pageable
    );

    MarketInsightDto getMarketInsights();
}
