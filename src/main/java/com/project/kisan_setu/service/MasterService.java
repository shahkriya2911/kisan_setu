package com.project.kisan_setu.service;
import com.project.kisan_setu.dto.IdNameDto;
import com.project.kisan_setu.dto.ResponseDto.MasterDataResponseDto;

import java.util.List;

public interface MasterService {

    MasterDataResponseDto getAllMasters();
    List<IdNameDto> getDistrictsByState(Long stateId);

}