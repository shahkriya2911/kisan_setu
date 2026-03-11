package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.ChangingLanguageRequestDto;
import com.project.kisan_setu.service.ChangingLanguageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/changing_languages")
public class ChangingLanguageController {
    private static final Logger logger = LoggerFactory.getLogger(ChangingLanguageController.class);
    private final ChangingLanguageService changingLanguageService;

    public ChangingLanguageController(ChangingLanguageService changingLanguageService) {
        this.changingLanguageService = changingLanguageService;
    }

    @GetMapping
    public ResponseEntity<?> getMyChangingLanguage() {
        return ResponseEntity.ok(changingLanguageService.getMyChangingLanguage());
    }

    @PostMapping
    public ResponseEntity<?> postChangingLanguage(@RequestBody ChangingLanguageRequestDto changingLanguageRequestDto){
        return ResponseEntity.ok(changingLanguageService.postChangingLanguage(changingLanguageRequestDto));
    }

    @PutMapping("/me")
    public ResponseEntity<?> upsertChangingLanguage(@RequestBody ChangingLanguageRequestDto changingLanguageRequestDto){
        return ResponseEntity.ok(changingLanguageService.postChangingLanguage(changingLanguageRequestDto));
    }

    @PutMapping("/{changingLanguageId}")
    public ResponseEntity<?> updateChangingLanguage(@PathVariable Long changingLanguageId,@RequestBody ChangingLanguageRequestDto changingLanguageRequestDto){
        return ResponseEntity.ok(changingLanguageService.updateChangingLanguage(changingLanguageId,changingLanguageRequestDto));
    }
}
