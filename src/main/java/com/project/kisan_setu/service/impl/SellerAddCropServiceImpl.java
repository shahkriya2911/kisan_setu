package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.entity.SellerAddCrop;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.repository.SellerAddCropRepository;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.service.SellerAddCropService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerAddCropServiceImpl implements SellerAddCropService {

    private final SellerAddCropRepository sellerAddCropRepository;
    private final UserRepository repository;

    @Override
    public SellerAddCrop saveCrop(SellerAddCrop crop) {
        // Make sure user exists
        if (crop.getUser() == null || crop.getUser().getUserId() == null) {
            throw new RuntimeException("User ID is required to save a crop");
        }
        // Fetch user from DB
        User user = repository.findById(crop.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        crop.setUser(user); // attach the full user entity
        return sellerAddCropRepository.save(crop);
    }

    @Override
    public List<SellerAddCrop> getAllCrops() {
        return sellerAddCropRepository.findAll();
    }

    @Override
    public SellerAddCrop getCropById(Long id) {
        return sellerAddCropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found with id: " + id));
    }

    @Override
    public SellerAddCrop updateCrop(Long id, SellerAddCrop crop) {

        SellerAddCrop existing =sellerAddCropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        existing.setCropName(crop.getCropName());
        existing.setQuantity(crop.getQuantity());
        existing.setBasePrice(crop.getBasePrice());
        existing.setHarvestDate(crop.getHarvestDate());
        existing.setVillage(crop.getVillage());
        existing.setTaluka(crop.getTaluka());
        existing.setDistrict(crop.getDistrict());
        existing.setState(crop.getState());
        existing.setStatus(crop.getStatus());

        return sellerAddCropRepository.save(existing);
    }



    @Override
    public void deleteCrop(Long id) {
        sellerAddCropRepository.deleteById(id);
    }
}
