package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.ResponseDto.CommodityListingDto;
import com.project.kisan_setu.service.AdminCommodityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Commodity Management", description = "Endpoints for admin commodity listing related resources")
public class AdminCommodityController {

    private final AdminCommodityService adminCommodityService;

    @GetMapping("/commodity-listings")
    @Operation(summary = "Get all commodity listings method", description = "Used by admin to get all commodity listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commodity listings fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<CommodityListingDto>> getAllCommodityListings() {
        return ResponseEntity.ok(adminCommodityService.getAllCommodityListings());
    }

    @GetMapping("/commodity-listings/active")
    @Operation(summary = "Get active commodity listings method", description = "Used by admin to get active commodity listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active commodity listings fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<CommodityListingDto>> getActiveCommodityListings() {
        return ResponseEntity.ok(adminCommodityService.getActiveCommodityListings());
    }

    @GetMapping("/commodity-listings/pending")
    @Operation(summary = "Get pending commodity listings method", description = "Used by admin to get pending commodity listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending commodity listings fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<CommodityListingDto>> getPendingCommodityListings() {
        return ResponseEntity.ok(adminCommodityService.getPendingCommodityListings());
    }

    @GetMapping("/commodity-listings/completed")
    @Operation(summary = "Get completed commodity listings method", description = "Used by admin to get completed commodity listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Completed commodity listings fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<CommodityListingDto>> getCompletedCommodityListings() {
        return ResponseEntity.ok(adminCommodityService.getCompletedCommodityListings());
    }
}
