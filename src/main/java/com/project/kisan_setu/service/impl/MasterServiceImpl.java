package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.RequestDto.IdNameDto;
import com.project.kisan_setu.dto.ResponseDto.MasterDataResponseDto;
import com.project.kisan_setu.mapper.MasterMapper;
import com.project.kisan_setu.repository.*;
import com.project.kisan_setu.service.MasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterServiceImpl implements MasterService {

    private final CropRepository cropRepository;
    private final UnitRepository unitRepository;
    private final StorageRepository storageRepository;
    private final PackagingRepository packagingRepository;
    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;

    @Override
    public MasterDataResponseDto getAllMasters() {

        MasterDataResponseDto response = new MasterDataResponseDto();

        response.setCrops(
                cropRepository.findAll().stream()
                        .map(c -> MasterMapper.toDto(c.getCropId(), c.getCropName()))
                        .toList()
        );

        response.setUnits(
                unitRepository.findAll().stream()
                        .map(u -> MasterMapper.toDto(u.getUnitId(), u.getUnitName()))
                        .toList()
        );

        response.setPackagingTypes(
                packagingRepository.findAll().stream()
                        .map(p -> MasterMapper.toDto(p.getPackagingId(), p.getPackagingType()))
                        .toList()
        );

        response.setStorageTypes(
                storageRepository.findAll().stream()
                        .map(s -> MasterMapper.toDto(s.getStorageId(), s.getStorageType()))
                        .toList()
        );

        response.setStates(
                stateRepository.findAll().stream()
                        .map(s -> MasterMapper.toDto(s.getStateId(), s.getName()))
                        .toList()
        );

        return response;
    }

    @Override
    public List<IdNameDto> getDistrictsByState(Long stateId) {
        return districtRepository.findByStateStateId(stateId)
                .stream()
                .map(d -> MasterMapper.toDto(d.getDistrictId(), d.getName()))
                .toList();
    }
}