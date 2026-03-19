package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ResponseDto.DisputeDto;

import java.util.List;

public interface AdminDisputeService {
    List<DisputeDto> getAllDisputes(String status);
    List<DisputeDto> getOpenDisputes();
    List<DisputeDto> getResolvedDisputes();
    List<DisputeDto> getUnderReviewDisputes();
}
