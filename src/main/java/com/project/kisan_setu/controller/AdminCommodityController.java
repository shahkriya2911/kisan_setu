package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.ResponseDto.CommodityListingDto;
import com.project.kisan_setu.service.AdminCommodityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCommodityController {

    private final AdminCommodityService adminCommodityService;

    @GetMapping("/commodity-listings")
    public ResponseEntity<List<CommodityListingDto>> getAllCommodityListings() {
        return ResponseEntity.ok(adminCommodityService.getAllCommodityListings());
    }

    @GetMapping("/commodity-listings/active")
    public ResponseEntity<List<CommodityListingDto>> getActiveCommodityListings() {
        return ResponseEntity.ok(adminCommodityService.getActiveCommodityListings());
    }

    @GetMapping("/commodity-listings/pending")
    public ResponseEntity<List<CommodityListingDto>> getPendingCommodityListings() {
        return ResponseEntity.ok(adminCommodityService.getPendingCommodityListings());
    }

    @GetMapping("/commodity-listings/completed")
    public ResponseEntity<List<CommodityListingDto>> getCompletedCommodityListings() {
        return ResponseEntity.ok(adminCommodityService.getCompletedCommodityListings());
    }
}
