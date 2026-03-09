package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.MarketFilterRequestDto;
import com.project.kisan_setu.dto.MarketInsightDto;
import com.project.kisan_setu.dto.MarketListingResponseDto;
import com.project.kisan_setu.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(MarketController.class);

    @GetMapping("/listings")
    public ResponseEntity<Page<MarketListingResponseDto>> getListings(
            MarketFilterRequestDto filter,
            @PageableDefault(size = 6, sort = "auctionEndTime") Pageable pageable) {
        logger.info("Get listings for market dashboard request attempt for user");
        logger.info("Fetched all listings for market dashboard successfully");
        return ResponseEntity.ok(
                marketService.getLiveListings(filter, pageable)
        );
    }

    @GetMapping("/insights")
    public ResponseEntity<MarketInsightDto> getInsights() {
        logger.info("Get market insights for user request attempt");
        logger.info("Market insights for user fetched successfully");
        return ResponseEntity.ok(marketService.getMarketInsights());
    }
}
