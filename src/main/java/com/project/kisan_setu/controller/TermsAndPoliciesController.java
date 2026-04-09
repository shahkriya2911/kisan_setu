package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.ResponseDto.TermsAndPoliciesResponseDto;
import com.project.kisan_setu.service.TermsAndPoliciesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/termsandpolicies")
@Tag(name = "Terms And Policies Management", description = "Endpoints for terms and policies related resources")
public class TermsAndPoliciesController {
    private final TermsAndPoliciesService termsAndPoliciesService;

    public TermsAndPoliciesController(TermsAndPoliciesService termsAndPoliciesService) {
        this.termsAndPoliciesService = termsAndPoliciesService;
    }

    @GetMapping
    @Operation(summary = "Get terms and policies method", description = "Used by user to get terms and policies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Terms and policies fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<TermsAndPoliciesResponseDto> getTermsAndPolicies(){
        return ResponseEntity.ok(termsAndPoliciesService.getTermsAndPolicies());
    }
}
