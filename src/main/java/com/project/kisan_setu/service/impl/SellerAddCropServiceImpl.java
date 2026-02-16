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

    private final SellerAddCropRepository repository;
    private final UserRepository userRepository;

    @Override
    public SellerAddCrop saveCrop(SellerAddCrop crop) {
        if (crop.getUser() == null || crop.getUser().getUserId() == null) {
            throw new RuntimeException("User ID must not be null");
        }
        Long userId = crop.getUser().getUserId();

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        crop.setUser(existingUser);

        crop.setCreatedAt(LocalDateTime.now());
        return repository.save(crop);
    }

    @Override
    public List<SellerAddCrop> getAllCrops() {
        return repository.findAll();
    }

    @Override
    public SellerAddCrop getCropById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found with id: " + id));
    }

    @Override
    public SellerAddCrop updateCrop(Long id, SellerAddCrop crop) {

        SellerAddCrop existing = repository.findById(id)
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

        return repository.save(existing);
    }



    @Override
    public void deleteCrop(Long id) {
        repository.deleteById(id);
    }
}
