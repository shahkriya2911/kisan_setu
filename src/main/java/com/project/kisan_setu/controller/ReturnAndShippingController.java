package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.ReturnAndShippingRequestDto;
import com.project.kisan_setu.dto.ResponseDto.ReturnAndShippingResponseDto;
import com.project.kisan_setu.service.ReturnAndShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/returnandshipping")
@Tag(name = "Return And Shipping Management", description = "Endpoints for return and shipping related resources")
public class ReturnAndShippingController {
    private final ReturnAndShippingService returnAndShippingService;

    public ReturnAndShippingController(ReturnAndShippingService returnAndShippingService) {
        this.returnAndShippingService = returnAndShippingService;
    }

    @GetMapping
    @Operation(summary = "Get return and shipping method", description = "Used by user to get their return and shipping details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return and shipping details fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<ReturnAndShippingResponseDto>> getMyReturnAndShipping() {
        return ResponseEntity.ok(returnAndShippingService.getMyReturnAndShipping());
    }

    @GetMapping("/{returnAndShippingId}")
    @Operation(summary = "Get return and shipping by ID method", description = "Used by user to get return and shipping details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return and shipping details fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Return and shipping record not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<ReturnAndShippingResponseDto> getReturnAndShippingById(
            @Parameter(description = "Return and shipping ID request", required = true)
            @PathVariable Long returnAndShippingId) {
        return ResponseEntity.ok(returnAndShippingService.getReturnAndShippingById(returnAndShippingId));
    }

    @PostMapping
    @Operation(summary = "Create return and shipping method", description = "Used by user to create return and shipping details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return and shipping details created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid return and shipping details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<ReturnAndShippingResponseDto> postReturnAndShipping(
            @Parameter(description = "Return and shipping details", required = true)
            @RequestBody ReturnAndShippingRequestDto returnAndShippingRequestDto) {
        return ResponseEntity.ok(returnAndShippingService.postReturnAndShipping(returnAndShippingRequestDto));
    }

    @PutMapping("/{returnAndShippingId}")
    @Operation(summary = "Update return and shipping method", description = "Used by user to update return and shipping details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return and shipping details updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid return and shipping details"),
            @ApiResponse(responseCode = "404", description = "Return and shipping record not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<ReturnAndShippingResponseDto> updateReturnAndShippingById(
            @Parameter(description = "Return and shipping ID request", required = true)
            @PathVariable Long returnAndShippingId,
            @Parameter(description = "Return and shipping details", required = true)
            @RequestBody ReturnAndShippingRequestDto returnAndShippingRequestDto){
        return ResponseEntity.ok(returnAndShippingService.updateReturnAndShipping(returnAndShippingId,returnAndShippingRequestDto));
    }

    @DeleteMapping("/{returnAndShippingId}")
    @Operation(summary = "Delete return and shipping method", description = "Used by user to delete return and shipping details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return and shipping deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Return and shipping record not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<String> deleteReturnAndShippingById(
            @Parameter(description = "Return and shipping ID request", required = true)
            @PathVariable Long returnAndShippingId) {
        returnAndShippingService.deleteReturnAndShipping(returnAndShippingId);
        return ResponseEntity.ok("Return and Shipping deleted successfully");
    }
}
