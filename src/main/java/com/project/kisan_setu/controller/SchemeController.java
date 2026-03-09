package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.SchemeRequestDto;
import com.project.kisan_setu.dto.SchemeResponseDto;
import com.project.kisan_setu.entity.Scheme;
import com.project.kisan_setu.service.SchemeService;
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
public class SchemeController {

    private final SchemeService schemeService;
    private static final Logger logger= LoggerFactory.getLogger(SchemeController.class);

    @PostMapping
    public ResponseEntity<SchemeResponseDto> create(@RequestBody SchemeRequestDto dto) {
        logger.info("Creating new scheme with title: {}", dto.getSchemeTitle());
        SchemeResponseDto response = schemeService.createScheme(dto);
        logger.info("Scheme created successfully with ID: {}", response.getSchemeId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchemeResponseDto> getById(@PathVariable Long id) {
        logger.debug("Get scheme with id : {} request attempt",id);
        logger.info("Fetching scheme with ID: {} successful", id);
        return ResponseEntity.ok(schemeService.getSchemeById(id));
    }

    @GetMapping
    public ResponseEntity<List<SchemeResponseDto>> getAll() {
        logger.info("Get all schemes request attempt");
        logger.info("Fetching all schemes successful");
        return ResponseEntity.ok(schemeService.getAllSchemes());
    }

    @DeleteMapping("/{schemeId}")
    public ResponseEntity<String> deleteScheme(@PathVariable Long schemeId)
    {
        logger.warn("Deleting scheme with ID: {}", schemeId);
        schemeService.deleteScheme(schemeId);
        logger.info("Scheme deleted successfully");
        return ResponseEntity.ok("Scheme deleted successfully");
    }
    
    @PutMapping("/{schemeId}")
    public ResponseEntity<SchemeResponseDto> updateSchemeById(@PathVariable Long schemeId,@RequestBody SchemeRequestDto dto){
        logger.info("Update scheme with id : {} request attempt",schemeId);
        logger.info("Updating scheme with ID: {} successful", schemeId);
        return ResponseEntity.ok(schemeService.updateSchemeById(schemeId, dto));
    }
}
