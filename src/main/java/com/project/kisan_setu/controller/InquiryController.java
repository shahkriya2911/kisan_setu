package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.RequestDto.InquiryRequestDto;
import com.project.kisan_setu.dto.ResponseDto.InquiryResponseDto;
import com.project.kisan_setu.service.InquiryService;
import com.project.kisan_setu.util.ValidatorMethods;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;
    private final ValidatorMethods validatorMethods;
    private static final Logger logger = LoggerFactory.getLogger(InquiryController.class);
    @PostMapping("/create")
    public ResponseEntity<?> createInquiry(@RequestBody @Valid InquiryRequestDto request) {
        logger.debug("Create inquiry request attempt for user");
        Long userId = validatorMethods.getCurrentUserId();

        InquiryResponseDto response =
                inquiryService.createInquiry(request, userId);
        logger.info("Inquiry created successfully");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/seller")
    public ResponseEntity<List<InquiryResponseDto>> getSellerInquiries() {
        logger.info("Get all seller inquires request attempt");
        Long userId = validatorMethods.getCurrentUserId();
        List<InquiryResponseDto> inquiries = inquiryService.getSellerInquiries(userId);
        logger.info("Fetched all seller inquires successfully");
        return ResponseEntity.ok(inquiries);
    }
}
