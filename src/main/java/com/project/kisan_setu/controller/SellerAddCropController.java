package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.SellerAddCropDto;
import com.project.kisan_setu.entity.SellerAddCrop;
import com.project.kisan_setu.service.SellerAddCropService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/crops")


public class SellerAddCropController {
    private final SellerAddCropService sellerAddCropService;

    public SellerAddCropController(SellerAddCropService sellerAddCropService) {
        this.sellerAddCropService = sellerAddCropService;
    }


    @PostMapping
    public SellerAddCropDto createCrop(@RequestBody SellerAddCropDto dto) {
        return sellerAddCropService.createCrop(dto);
    }

    @GetMapping
    public List<SellerAddCrop> getAllCrops() {
        return sellerAddCropService.getAllCrops();
    }


    @GetMapping("/{id}")
    public SellerAddCrop getCropById(@PathVariable Long id) {
        return sellerAddCropService.getCropById(id);
    }

    @PutMapping("/{id}")
    public SellerAddCrop updateCrop(
            @PathVariable Long id,
            @RequestBody SellerAddCrop crop) {

        return sellerAddCropService.updateCrop(id, crop);
    }


    @DeleteMapping("/{id}")
    public String deleteCrop(@PathVariable Long id) {
       sellerAddCropService.deleteCrop(id);
        return "Crop deleted successfully";
    }

}
