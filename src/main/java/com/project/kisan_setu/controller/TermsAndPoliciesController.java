package com.project.kisan_setu.controller;
import com.project.kisan_setu.service.TermsAndPoliciesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/termsandpolicies")
public class TermsAndPoliciesController {

    private static final Logger logger = LoggerFactory.getLogger(TermsAndPoliciesController.class);
    private final TermsAndPoliciesService termsAndPoliciesService;

    public TermsAndPoliciesController(TermsAndPoliciesService termsAndPoliciesService) {
        this.termsAndPoliciesService = termsAndPoliciesService;
    }

    @GetMapping
    public ResponseEntity<?> getTermsAndPolicies(){
        return ResponseEntity.ok(termsAndPoliciesService.getTermsAndPolicies());
    }
}
