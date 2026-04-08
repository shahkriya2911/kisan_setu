package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.RequestDto.SchemeRequestDto;
import com.project.kisan_setu.dto.ResponseDto.SchemeResponseDto;
import com.project.kisan_setu.service.SchemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/schemes")
@RequiredArgsConstructor
@Tag(name = "Scheme Management", description = "Endpoints for scheme related resources")
public class SchemeController {

    private final SchemeService schemeService;
    private static final Logger logger= LoggerFactory.getLogger(SchemeController.class);

    @PostMapping
    @Operation(summary = "Create scheme method", description = "Used to create a scheme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scheme created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid scheme details"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    public ResponseEntity<SchemeResponseDto> create(
            @Parameter(description = "Scheme details", required = true)
            @RequestBody SchemeRequestDto dto) {
        logger.info("Creating new scheme with title: {}", dto.getSchemeTitle());
        SchemeResponseDto response = schemeService.createScheme(dto);
        logger.info("Scheme created successfully with ID: {}", response.getSchemeId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get scheme by ID method", description = "Used to get scheme details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scheme fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Scheme not found"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    public ResponseEntity<SchemeResponseDto> getById(
            @Parameter(description = "Scheme ID request", required = true)
            @PathVariable Long id) {
        logger.debug("Get scheme with id : {} request attempt",id);
        logger.info("Fetching scheme with ID: {} successful", id);
        return ResponseEntity.ok(schemeService.getSchemeById(id));
    }

    @GetMapping
    @Operation(summary = "Get all schemes method", description = "Used to get all schemes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schemes fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    public ResponseEntity<List<SchemeResponseDto>> getAll() {
        logger.info("Get all schemes request attempt");
        logger.info("Fetching all schemes successful");
        return ResponseEntity.ok(schemeService.getAllSchemes());
    }

    @DeleteMapping("/{schemeId}")
    @Operation(summary = "Delete scheme method", description = "Used to delete a scheme by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scheme deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Scheme not found"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    public ResponseEntity<String> deleteScheme(
            @Parameter(description = "Scheme ID request", required = true)
            @PathVariable Long schemeId)
    {
        logger.warn("Deleting scheme with ID: {}", schemeId);
        schemeService.deleteScheme(schemeId);
        logger.info("Scheme deleted successfully");
        return ResponseEntity.ok("Scheme deleted successfully");
    }
    
    @PutMapping("/{schemeId}")
    @Operation(summary = "Update scheme method", description = "Used to update a scheme by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scheme updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid scheme details"),
            @ApiResponse(responseCode = "404", description = "Scheme not found"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    public ResponseEntity<SchemeResponseDto> updateSchemeById(
            @Parameter(description = "Scheme ID request", required = true)
            @PathVariable Long schemeId,
            @Parameter(description = "Updated scheme details", required = true)
            @RequestBody SchemeRequestDto dto){
        logger.info("Update scheme with id : {} request attempt",schemeId);
        logger.info("Updating scheme with ID: {} successful", schemeId);
        return ResponseEntity.ok(schemeService.updateSchemeById(schemeId, dto));
    }
}
