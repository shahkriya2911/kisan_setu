package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.InquiryRequestDto;
import com.project.kisan_setu.dto.InquiryResponseDto;
import com.project.kisan_setu.entity.BuyerInquiry;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.service.InquiryService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;
    private final ValidatorMethods validatorMethods;
    @PostMapping("/create")
    public ResponseEntity<?> createInquiry(@RequestBody InquiryRequestDto request) {

        String email = validatorMethods.getCurrentUserEmail();

        InquiryResponseDto response =
                inquiryService.createInquiry(request, email);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/seller")
    public ResponseEntity<List<InquiryResponseDto>> getSellerInquiries() {
        String email = validatorMethods.getCurrentUserEmail();
        List<InquiryResponseDto> inquiries = inquiryService.getSellerInquiries(email);
        return ResponseEntity.ok(inquiries);
    }
}
