package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.BuyingRequirementResponseDto;
import com.project.kisan_setu.entity.BuyingRequirement;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.mapper.BuyingRequirementMapper;
import com.project.kisan_setu.repository.BuyingRequirementRepository;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.service.BuyingRequirementService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuyingRequirementServiceImpl implements BuyingRequirementService {

    private final BuyingRequirementRepository requirementRepository;
    private final UserRepository userRepository;
    private final BuyingRequirementMapper mapper;

    public BuyingRequirementServiceImpl(BuyingRequirementRepository requirementRepository,
                                        UserRepository userRepository,
                                        BuyingRequirementMapper mapper) {
        this.requirementRepository = requirementRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    // Create
    public BuyingRequirementResponseDto createRequirement(BuyingRequirementRequestDto dto) {
        User buyer = userRepository.findById(dto.getBuyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        BuyingRequirement br = mapper.toEntity(dto);
        br.setBuyer(buyer);
        br.setCreatedAt(LocalDateTime.now());
        BuyingRequirement saved = requirementRepository.save(br);
        return mapper.toDto(saved);
    }

    // Read all
    public List<BuyingRequirementResponseDto> getAllRequirements() {
        return requirementRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // Read one
    public BuyingRequirementResponseDto getRequirementById(Long id) {
        BuyingRequirement br = requirementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requirement not found"));
        return mapper.toDto(br);
    }

    // Update
    public BuyingRequirementResponseDto updateRequirement(Long id, BuyingRequirementRequestDto dto) {
        BuyingRequirement br = requirementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requirement not found"));
        User buyer = userRepository.findById(dto.getBuyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        br.setBuyer(buyer);
        br.setCropType(dto.getCropType());
        br.setQuantity(dto.getQuantity());
        br.setMinPrice(dto.getMinPrice());
        br.setMaxPrice(dto.getMaxPrice());
        br.setQualityGrade(dto.getQualityGrade());
        br.setDelieveryLocation(dto.getDelieveryLocation());
        br.setDeadline(dto.getDeadline());
        br.setUpdatedAt(LocalDateTime.now());

        return mapper.toDto(requirementRepository.save(br));
    }

    // Delete
    public void deleteRequirement(Long id) {
        BuyingRequirement br = requirementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requirement not found"));
        requirementRepository.delete(br);
    }
}
