package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.IdNameDto;
import com.project.kisan_setu.service.MasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
@Tag(name = "Master Data Management", description = "Endpoints for master data related resources")
public class MasterController {
    private final MasterService masterService;


    @GetMapping("/crops")
    @Operation(summary = "Get all crops method", description = "Used to get all crop master data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Crops fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public List<IdNameDto> getAllCrops() {
        return masterService.getAllMasters().getCrops();
    }

    @GetMapping("/units")
    @Operation(summary = "Get all units method", description = "Used to get all unit master data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Units fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public List<IdNameDto> getAllUnits() {
        return masterService.getAllMasters().getUnits();
    }

    @GetMapping("/packaging")
    @Operation(summary = "Get all packaging types method", description = "Used to get all packaging master data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Packaging types fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public List<IdNameDto> getAllPackaging() {
        return masterService.getAllMasters().getPackagingTypes();
    }

    @GetMapping("/storage")
    @Operation(summary = "Get all storage types method", description = "Used to get all storage master data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Storage types fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public List<IdNameDto> getAllStorage() {
        return masterService.getAllMasters().getStorageTypes();
    }


    @GetMapping("/states")
    @Operation(summary = "Get all states method", description = "Used to get all state master data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "States fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public List<IdNameDto> getAllStates() {
        return masterService.getAllMasters().getStates();
    }

    @GetMapping("/districts/{stateId}")
    @Operation(summary = "Get districts by state method", description = "Used to get all districts for a given state")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Districts fetched successfully"),
            @ApiResponse(responseCode = "404", description = "State not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public List<IdNameDto> getDistrictsByState(
            @Parameter(description = "State ID request", required = true)
            @PathVariable Long stateId) {
        return masterService.getDistrictsByState(stateId);
    }
}
