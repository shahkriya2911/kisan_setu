package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.MarketFilterRequestDto;
import com.project.kisan_setu.dto.MarketInsightDto;
import com.project.kisan_setu.dto.MarketListingResponseDto;
import com.project.kisan_setu.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/market")
@RequiredArgsConstructor
public class MarketController {

    @Autowired
    private final MarketService marketService;

    @GetMapping("/listings")
    public ResponseEntity<Page<MarketListingResponseDto>> getListings(
            MarketFilterRequestDto filter,
            @PageableDefault(size = 6, sort = "auctionEndTime") Pageable pageable) {

        return ResponseEntity.ok(
                marketService.getLiveListings(filter, pageable)
        );
    }

    @GetMapping("/insights")
    public ResponseEntity<MarketInsightDto> getInsights() {
        return ResponseEntity.ok(marketService.getMarketInsights());
    }
}
