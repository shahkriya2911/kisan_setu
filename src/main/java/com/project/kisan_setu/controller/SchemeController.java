package com.project.kisan_setu.controller;

import com.project.kisan_setu.entity.Scheme;
import com.project.kisan_setu.service.SchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/schemes")
@RequiredArgsConstructor
public class SchemeController {

    private final SchemeService schemeService;

    @PostMapping
    public Scheme create(@RequestBody Scheme scheme) {
        return schemeService.createScheme(scheme);
    }

    @GetMapping("/{id}")
    public Scheme getById(@PathVariable Long id) {
        return schemeService.getSchemeById(id);
    }

    @GetMapping
    public List<Scheme> getAll() {
        return schemeService.getAllSchemes();
    }
    
    @DeleteMapping("/{schemeId}")
    public void deleteSchemeById(@PathVariable Long schemeId){
        schemeService.deleteSchemeById(schemeId);
    }
    
    @PutMapping("/{schemeId}")
    public Scheme updateSchemeById(@PathVariable Long schemeId,@RequestBody Scheme scheme){
        return schemeService.updateSchemeById(schemeId,scheme);
    }
}
