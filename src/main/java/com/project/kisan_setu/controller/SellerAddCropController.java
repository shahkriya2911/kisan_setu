package com.project.kisan_setu.controller;

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
    public SellerAddCrop createCrop(@RequestBody SellerAddCrop crop) {
        return service.saveCrop(crop);
    }


    @GetMapping
    public List<SellerAddCrop> getAllCrops() {
        return service.getAllCrops();
    }


    @GetMapping("/{id}")
    public SellerAddCrop getCropById(@PathVariable Long id) {
        return service.getCropById(id);
    }

    @PutMapping("/{id}")
    public SellerAddCrop updateCrop(@PathVariable Long id,
                                    @RequestBody SellerAddCrop crop) {
        return service.updateCrop(id, crop);
    }
    @DeleteMapping("/{id}")
    public String deleteCrop(@PathVariable Long id) {
        service.deleteCrop(id);
        return "Crop deleted successfully!";
    }
}
