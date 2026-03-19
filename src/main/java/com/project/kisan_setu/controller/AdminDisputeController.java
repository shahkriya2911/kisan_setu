package com.project.kisan_setu.controller;

import com.project.kisan_setu.service.AdminDisputeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin-dispute")
@RequiredArgsConstructor
public class AdminDisputeController {
    private final AdminDisputeService adminDisputeService;

    @GetMapping
    public ResponseEntity<?> getDisputes(@RequestParam(required = false)String status)
    {
        return ResponseEntity.ok(adminDisputeService.getAllDisputes(status));
    }
    @GetMapping("/open")
    public ResponseEntity<?> getOpenDispute(@RequestParam(required = false)String status)
    {
        return ResponseEntity.ok(adminDisputeService.getOpenDisputes());
    }
    @GetMapping("/closed")
    public ResponseEntity<?> getClosedDispute(@RequestParam(required = false)String status)
    {
        return ResponseEntity.ok(adminDisputeService.getResolvedDisputes());
    }
    @GetMapping("/under-review")
    public ResponseEntity<?> getUnderReviewDispute(@RequestParam(required = false)String status)
    {
        return ResponseEntity.ok(adminDisputeService.getUnderReviewDisputes());
    }


}
