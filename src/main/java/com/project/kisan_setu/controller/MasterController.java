package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.IdNameDto;
import com.project.kisan_setu.service.MasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
public class MasterController {
    private final MasterService masterService;


    @GetMapping("/crops")
    public List<IdNameDto> getAllCrops() {
        return masterService.getAllMasters().getCrops();
    }

    @GetMapping("/units")
    public List<IdNameDto> getAllUnits() {
        return masterService.getAllMasters().getUnits();
    }

    @GetMapping("/packaging")
    public List<IdNameDto> getAllPackaging() {
        return masterService.getAllMasters().getPackagingTypes();
    }

    @GetMapping("/storage")
    public List<IdNameDto> getAllStorage() {
        return masterService.getAllMasters().getStorageTypes();
    }


    @GetMapping("/states")
    public List<IdNameDto> getAllStates() {
        return masterService.getAllMasters().getStates();
    }

    @GetMapping("/districts/{stateId}")
    public List<IdNameDto> getDistrictsByState(@PathVariable Long stateId) {
        return masterService.getDistrictsByState(stateId);
    }
}