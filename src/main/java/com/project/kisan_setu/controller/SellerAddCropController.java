package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.SellerAddCropRequestDto;
import com.project.kisan_setu.dto.SellerAddCropResponseDto;
import com.project.kisan_setu.entity.SellerAddCrop;
import com.project.kisan_setu.service.SellerAddCropService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/crops")
public class SellerAddCropController {
    private final SellerAddCropService service;

    public SellerAddCropController(SellerAddCropService service) {
        this.service = service;
    }


    @PostMapping
    public SellerAddCropResponseDto createCrop(@RequestBody SellerAddCropRequestDto dto) {
        return service.saveCrop(dto);
    }

    @GetMapping
    public List<SellerAddCropResponseDto> getAllCrops() {
        return service.getAllCrops();
    }


    @GetMapping("/{id}")
    public SellerAddCropResponseDto getCropById(@PathVariable Long id) {
        return service.getCropById(id);
    }

    @PutMapping("/{id}")
    public SellerAddCropResponseDto updateCrop(@PathVariable Long id,
                                    @RequestBody SellerAddCropRequestDto dto) {
        return service.updateCrop(id,dto);
    }
    @DeleteMapping("/{id}")
    public String deleteCrop(@PathVariable Long id) {
        service.deleteCrop(id);
        return "Crop deleted successfully!";
    }
}
