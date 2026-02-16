package com.project.kisan_setu.service;

import com.project.kisan_setu.entity.Scheme;

import java.util.List;

public interface SchemeService {
    Scheme createScheme(Scheme scheme);
    Scheme getSchemeById(Long schemeId);

    List<Scheme> getAllSchemes();

    Scheme updateSchemeById(Long schemeId, Scheme updatedScheme);

    void deleteSchemeById(Long schemeId);
}
