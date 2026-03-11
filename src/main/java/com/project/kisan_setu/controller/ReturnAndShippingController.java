package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.ReturnAndShippingRequestDto;
import com.project.kisan_setu.service.ReturnAndShippingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/returnandshipping")
public class ReturnAndShippingController {
    private static final Logger logger = LoggerFactory.getLogger(ReturnAndShippingController.class);
    private final ReturnAndShippingService returnAndShippingService;

    public ReturnAndShippingController(ReturnAndShippingService returnAndShippingService) {
        this.returnAndShippingService = returnAndShippingService;
    }

    @GetMapping
    public ResponseEntity<?> getMyReturnAndShipping() {
        return ResponseEntity.ok(returnAndShippingService.getMyReturnAndShipping());
    }

    @GetMapping("/{returnAndShippingId}")
    public ResponseEntity<?> getReturnAndShippingById(@PathVariable Long returnAndShippingId) {
        return ResponseEntity.ok(returnAndShippingService.getReturnAndShippingById(returnAndShippingId));
    }

    @PostMapping
    public ResponseEntity<?> postReturnAndShipping(@RequestBody ReturnAndShippingRequestDto returnAndShippingRequestDto) {
        return ResponseEntity.ok(returnAndShippingService.postReturnAndShipping(returnAndShippingRequestDto));
    }

    @PutMapping("/{returnAndShippingId}")
    public ResponseEntity<?> updateReturnAndShippingById(@PathVariable Long returnAndShippingId,@RequestBody ReturnAndShippingRequestDto returnAndShippingRequestDto){
        return ResponseEntity.ok(returnAndShippingService.updateReturnAndShipping(returnAndShippingId,returnAndShippingRequestDto));
    }

    @DeleteMapping("/{returnAndShippingId}")
    public ResponseEntity<?> deleteReturnAndShippingById(@PathVariable Long returnAndShippingId) {
        returnAndShippingService.deleteReturnAndShipping(returnAndShippingId);
        return ResponseEntity.ok("Return and Shipping deleted successfully");
    }
}
