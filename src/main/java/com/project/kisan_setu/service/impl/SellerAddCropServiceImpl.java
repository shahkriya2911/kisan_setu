package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.SellerAddCropDto;
import com.project.kisan_setu.entity.SellerAddCrop;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.mapper.SellerAddCropMapper;
import com.project.kisan_setu.repository.SellerAddCropRepository;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.service.SellerAddCropService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SellerAddCropServiceImpl implements SellerAddCropService {
    private final SellerAddCropRepository sellerAddCropRepository;
    private final UserRepository userRepository;

    public SellerAddCropServiceImpl(SellerAddCropRepository sellerAddCropRepository, UserRepository userRepository) {
        this.sellerAddCropRepository = sellerAddCropRepository;
        this.userRepository = userRepository;
    }

    @Override
    public SellerAddCropDto createCrop(SellerAddCropDto dto) {
        User seller = userRepository.findById(dto.getSellerId())
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        SellerAddCrop crop = SellerAddCropMapper.toEntity(dto, seller);
        crop.setCreatedAt(LocalDateTime.now());
        crop.setStatus("AVAILABLE");

        return SellerAddCropMapper.toDto(sellerAddCropRepository.save(crop));
    }
    @Override
    public List<SellerAddCrop> getAllCrops() {
        return sellerAddCropRepository.findAll();
    }

    @Override
    public SellerAddCrop getCropById(Long id) {
        return sellerAddCropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found"));
    }

    @Override
    public SellerAddCrop updateCrop(Long id, SellerAddCrop updatedCrop) {

        SellerAddCrop existing = sellerAddCropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        existing.setCropName(updatedCrop.getCropName());
        existing.setQuantity(updatedCrop.getQuantity());
        existing.setBasePrice(updatedCrop.getBasePrice());
        existing.setHarvestDate(updatedCrop.getHarvestDate());
        existing.setVillage(updatedCrop.getVillage());
        existing.setTalika(updatedCrop.getTalika());
        existing.setDistrict(updatedCrop.getDistrict());
        existing.setState(updatedCrop.getState());
        existing.setStatus(updatedCrop.getStatus());

        return sellerAddCropRepository.save(existing);
    }
    @Override
    public void deleteCrop(Long id) {

        SellerAddCrop crop = sellerAddCropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        sellerAddCropRepository.delete(crop);
    }




}
