package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.entity.Scheme;
import com.project.kisan_setu.repository.SchemeRepository;
import com.project.kisan_setu.service.SchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchemeServiceImpl implements SchemeService {

    private final SchemeRepository schemeRepository;

    @Override
    public Scheme createScheme(Scheme scheme) {
        return schemeRepository.save(scheme);
    }

    @Override
    public Scheme getSchemeById(Long schemeId) {

        if (schemeId == null) {
            throw new IllegalArgumentException("Scheme ID cannot be null");
        }

        return schemeRepository.findById(schemeId)
                .orElseThrow(() -> new RuntimeException("Scheme not found with id: " + schemeId));
    }

    @Override
    public List<Scheme> getAllSchemes() {
        return schemeRepository.findAll();
    }

    @Override
    public Scheme updateSchemeById(Long schemeId, Scheme updatedScheme) {

        Scheme existingScheme = getSchemeById(schemeId);

        existingScheme.setSchemeTitle(updatedScheme.getSchemeTitle());
        existingScheme.setSchemeFullName(updatedScheme.getSchemeFullName());
        existingScheme.setSchemeCategory(updatedScheme.getSchemeCategory());
        existingScheme.setSchemeDescription(updatedScheme.getSchemeDescription());
        existingScheme.setSchemeBenefits(updatedScheme.getSchemeBenefits());
        existingScheme.setSchemeEligibility(updatedScheme.getSchemeEligibility());
        existingScheme.setSchemeState(updatedScheme.getSchemeState());
        existingScheme.setSchemeOfficialLink(updatedScheme.getSchemeOfficialLink());

        return schemeRepository.save(existingScheme);
    }

    @Override
    public void deleteSchemeById(Long schemeId) {

        Scheme existingScheme = getSchemeById(schemeId);
        schemeRepository.delete(existingScheme);
    }
}
