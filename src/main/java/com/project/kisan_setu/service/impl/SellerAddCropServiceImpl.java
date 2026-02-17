package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.SellerAddCropRequestDto;
import com.project.kisan_setu.dto.SellerAddCropResponseDto;
import com.project.kisan_setu.entity.SellerAddCrop;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.mapper.SellerAddCropMapper;
import com.project.kisan_setu.repository.SellerAddCropRepository;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.service.SellerAddCropService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerAddCropServiceImpl implements SellerAddCropService {

    private final SellerAddCropRepository repository;
    private final UserRepository userRepository;
    private final SellerAddCropMapper sellerAddCropMapper;

    @Override
    public SellerAddCropResponseDto saveCrop(SellerAddCropRequestDto dto) {
        User seller = userRepository.findById(dto.getSellerId())
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        SellerAddCrop crop = sellerAddCropMapper.toEntity(dto);
        crop.setSeller(seller);

        repository.save(crop);
        return sellerAddCropMapper.toDto(crop);
    }

    public List<SellerAddCropResponseDto> getAllCrops() {
        return repository.findAll().stream()
                .map(sellerAddCropMapper::toDto)
                .collect(Collectors.toList());
    }

    public SellerAddCropResponseDto getCropById(Long id) {
        SellerAddCrop crop = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found"));
        return sellerAddCropMapper.toDto(crop);
    }
    public SellerAddCropResponseDto updateCrop(Long id, SellerAddCropRequestDto dto) {
        SellerAddCrop crop = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        User seller = userRepository.findById(dto.getSellerId())
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        crop.setCropName(dto.getCropName());
        crop.setQuantity(dto.getQuantity());
        crop.setBasePrice(dto.getBasePrice());
        crop.setHarvestDate(dto.getHarvestDate());
        crop.setVillage(dto.getVillage());
        crop.setTaluka(dto.getTaluka());
        crop.setDistrict(dto.getDistrict());
        crop.setState(dto.getState());
        crop.setStatus(dto.getStatus());
        crop.setSeller(seller);

        repository.save(crop);
        return sellerAddCropMapper.toDto(crop);
    }
    public void deleteCrop(Long id) {
        repository.deleteById(id);
    }


}
