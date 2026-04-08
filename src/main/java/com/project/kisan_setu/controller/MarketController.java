package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.RequestDto.MarketFilterRequestDto;
import com.project.kisan_setu.dto.ResponseDto.MarketInsightDto;
import com.project.kisan_setu.dto.ResponseDto.MarketListingResponseDto;
import com.project.kisan_setu.service.MarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Market Management", description = "Endpoints for market related resources")
public class MarketController {

    @Autowired
    private final MarketService marketService;
    private static final Logger logger = LoggerFactory.getLogger(MarketController.class);

    @GetMapping("/listings")
    @Operation(summary = "Get market listings method", description = "Used by user to get market dashboard listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Market listings fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
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
    @Operation(summary = "Get market insights method", description = "Used by user to get market insights")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Market insights fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<MarketInsightDto> getInsights() {
        logger.info("Get market insights for user request attempt");
        logger.info("Market insights for user fetched successfully");
        return ResponseEntity.ok(marketService.getMarketInsights());
    }
}
