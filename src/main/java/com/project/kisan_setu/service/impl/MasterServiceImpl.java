package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.RequestDto.IdNameDto;
import com.project.kisan_setu.dto.ResponseDto.MasterDataResponseDto;
import com.project.kisan_setu.mapper.MasterMapper;
import com.project.kisan_setu.repository.CropRepository;
import com.project.kisan_setu.repository.DistrictRepository;
import com.project.kisan_setu.repository.PackagingRepository;
import com.project.kisan_setu.repository.StateRepository;
import com.project.kisan_setu.repository.StorageRepository;
import com.project.kisan_setu.repository.UnitRepository;
import com.project.kisan_setu.service.MasterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
    @Cacheable(value = "masterData", key = "'all'")
    public MasterDataResponseDto getAllMasters() {
        log.info("Cache miss - fetching all master data from DB");

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
    @Cacheable(value = "districtsByState", key = "#stateId")
    public List<IdNameDto> getDistrictsByState(Long stateId) {
        log.info("Cache miss - fetching districts for stateId: {} from DB", stateId);
        return districtRepository.findByStateStateId(stateId)
                .stream()
                .map(d -> MasterMapper.toDto(d.getDistrictId(), d.getName()))
                .toList();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "masterData", allEntries = true),
            @CacheEvict(value = "districtsByState", allEntries = true)
    })
    public void evictAllMasterCaches() {
        log.info("Evicting all master and district caches");
    }

    @Override
    @CacheEvict(value = "districtsByState", key = "#stateId")
    public void evictDistrictCache(Long stateId) {
        log.info("Evicting district cache for stateId: {}", stateId);
    }
}