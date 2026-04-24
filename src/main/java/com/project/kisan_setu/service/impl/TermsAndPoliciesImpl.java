package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.ResponseDto.TermsAndPoliciesResponseDto;
import com.project.kisan_setu.entity.TermsAndPolicies;
import com.project.kisan_setu.mapper.TermsAndPoliciesMapper;
import com.project.kisan_setu.repository.TermsAndPoliciesRepository;
import com.project.kisan_setu.service.TermsAndPoliciesService;
import org.springframework.stereotype.Service;

@Service
public class TermsAndPoliciesImpl implements TermsAndPoliciesService {
    private final TermsAndPoliciesRepository termsAndPoliciesRepository;

    public TermsAndPoliciesImpl(TermsAndPoliciesRepository termsAndPoliciesRepository) {
        this.termsAndPoliciesRepository = termsAndPoliciesRepository;
    }

    @Override
    public TermsAndPoliciesResponseDto getTermsAndPolicies(){
        TermsAndPolicies policies =  termsAndPoliciesRepository.findTopByOrderByTermsAndPoliciesIdAsc()
                .orElseThrow(() -> new RuntimeException("Policies not found"));
        return TermsAndPoliciesMapper.toDto(policies);

    }
}
