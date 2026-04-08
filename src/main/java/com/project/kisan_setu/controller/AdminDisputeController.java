package com.project.kisan_setu.controller;

import com.project.kisan_setu.service.AdminDisputeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin-dispute")
@RequiredArgsConstructor
@Tag(name = "Admin Dispute Management", description = "Endpoints for admin dispute related resources")
public class AdminDisputeController {
    private final AdminDisputeService adminDisputeService;

    @GetMapping
    @Operation(summary = "Get all disputes method", description = "Used by admin to get disputes with optional status filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disputes fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<?> getDisputes(
            @Parameter(description = "Dispute status filter", required = false)
            @RequestParam(required = false)String status)
    {
        return ResponseEntity.ok(adminDisputeService.getAllDisputes(status));
    }

    @GetMapping("/open")
    @Operation(summary = "Get open disputes method", description = "Used by admin to get all open disputes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Open disputes fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<?> getOpenDispute(
            @Parameter(description = "Dispute status filter", required = false)
            @RequestParam(required = false)String status)
    {
        return ResponseEntity.ok(adminDisputeService.getOpenDisputes());
    }

    @GetMapping("/closed")
    @Operation(summary = "Get closed disputes method", description = "Used by admin to get all closed disputes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Closed disputes fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<?> getClosedDispute(
            @Parameter(description = "Dispute status filter", required = false)
            @RequestParam(required = false)String status)
    {
        return ResponseEntity.ok(adminDisputeService.getResolvedDisputes());
    }

    @GetMapping("/under-review")
    @Operation(summary = "Get under review disputes method", description = "Used by admin to get disputes under review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Under review disputes fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<?> getUnderReviewDispute(
            @Parameter(description = "Dispute status filter", required = false)
            @RequestParam(required = false)String status)
    {
        return ResponseEntity.ok(adminDisputeService.getUnderReviewDisputes());
    }
}
