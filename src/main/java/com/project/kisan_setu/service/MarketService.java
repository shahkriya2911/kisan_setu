package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.RequestDto.MarketFilterRequestDto;
import com.project.kisan_setu.dto.ResponseDto.MarketInsightDto;
import com.project.kisan_setu.dto.ResponseDto.MarketListingResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketService {

    Page<MarketListingResponseDto> getLiveListings(
            MarketFilterRequestDto filter,
            Pageable pageable
    );

    MarketInsightDto getMarketInsights();
}
