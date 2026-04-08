package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.ChangingLanguageRequestDto;
import com.project.kisan_setu.service.ChangingLanguageService;
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

@RestController
@RequestMapping("api/changing_languages")
@Tag(name = "Changing Language Management", description = "Endpoints for changing language related resources")
public class ChangingLanguageController {
    private final ChangingLanguageService changingLanguageService;

    public ChangingLanguageController(ChangingLanguageService changingLanguageService) {
        this.changingLanguageService = changingLanguageService;
    }

    @GetMapping
    @Operation(summary = "Get changing language method", description = "Used by user to get their language preference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language preference fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<?> getMyChangingLanguage() {
        return ResponseEntity.ok(changingLanguageService.getMyChangingLanguage());
    }

    @PostMapping
    @Operation(summary = "Create changing language method", description = "Used by user to create a language preference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language preference created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid language preference details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<?> postChangingLanguage(
            @Parameter(description = "Language preference details", required = true)
            @RequestBody ChangingLanguageRequestDto changingLanguageRequestDto){
        return ResponseEntity.ok(changingLanguageService.postChangingLanguage(changingLanguageRequestDto));
    }

    @PutMapping("/me")
    @Operation(summary = "Upsert changing language method", description = "Used by user to create or update their language preference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language preference updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid language preference details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<?> upsertChangingLanguage(
            @Parameter(description = "Language preference details", required = true)
            @RequestBody ChangingLanguageRequestDto changingLanguageRequestDto){
        return ResponseEntity.ok(changingLanguageService.postChangingLanguage(changingLanguageRequestDto));
    }

    @PutMapping("/{changingLanguageId}")
    @Operation(summary = "Update changing language method", description = "Used by user to update a language preference by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language preference updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid language preference details"),
            @ApiResponse(responseCode = "404", description = "Language preference not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<?> updateChangingLanguage(
            @Parameter(description = "Changing language ID request", required = true)
            @PathVariable Long changingLanguageId,
            @Parameter(description = "Language preference details", required = true)
            @RequestBody ChangingLanguageRequestDto changingLanguageRequestDto){
        return ResponseEntity.ok(changingLanguageService.updateChangingLanguage(changingLanguageId,changingLanguageRequestDto));
    }
}
