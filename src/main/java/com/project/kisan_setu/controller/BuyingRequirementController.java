package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.BuyingRequirementResponseDto;
import com.project.kisan_setu.service.impl.BuyingRequirementServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requirements")
public class BuyingRequirementController {

    private final BuyingRequirementServiceImpl service;

    public BuyingRequirementController(BuyingRequirementServiceImpl service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BuyingRequirementResponseDto> create(@RequestBody BuyingRequirementRequestDto dto) {
        return ResponseEntity.ok(service.createRequirement(dto));
    }

    @GetMapping
    public ResponseEntity<List<BuyingRequirementResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAllRequirements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuyingRequirementResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getRequirementById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuyingRequirementResponseDto> update(@PathVariable Long id,
                                                               @RequestBody BuyingRequirementRequestDto dto) {
        return ResponseEntity.ok(service.updateRequirement(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteRequirement(id);
        return ResponseEntity.noContent().build();
    }
}
