package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.ReportUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.ReviewRequestDto;
import com.project.kisan_setu.dto.ResponseDto.OrderHistoryResponseDto;
import com.project.kisan_setu.service.OrderHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orderHistory")
@RequiredArgsConstructor
public class OrderHistoryController {
    private final OrderHistoryService orderHistoryService;
    @GetMapping
    public ResponseEntity<List<OrderHistoryResponseDto>> getAllOrderHistory() {
        return ResponseEntity.ok(orderHistoryService.getAllOrderHistory());
    }

    @GetMapping("/purchased")
    public ResponseEntity<List<OrderHistoryResponseDto>> getPurchasedOrderHistory() {
        return ResponseEntity.ok(orderHistoryService.getPurchasedOrderHistory());
    }

    @GetMapping("/sold")
    public ResponseEntity<List<OrderHistoryResponseDto>> getSoldOrderHistory() {
        return ResponseEntity.ok(orderHistoryService.getSoldOrderHistory());
    }

    @PostMapping("/{orderId}/review")
    public ResponseEntity<String> submitSellerReview(
            @PathVariable Long orderId,
            @Valid @RequestBody ReviewRequestDto requestDto) {
        return ResponseEntity.ok(orderHistoryService.submitSellerReview(orderId, requestDto));
    }

    @PostMapping("/report/{orderId}")
    public ResponseEntity<String> reportSeller(
            @PathVariable Long orderId,
            @RequestBody ReportUserRequestDto requestDto) {

        return ResponseEntity.ok(orderHistoryService.reportSeller(orderId, requestDto));
    }

    @PostMapping("/{orderId}/verify-delivery-otp")
    public ResponseEntity<?> verifyDeliveryOtp(@PathVariable Long orderId,
                                               @RequestParam Long sellerId,
                                               @RequestParam String otp) {

        return ResponseEntity.ok(
                orderHistoryService.verifyDeliveryOtp(orderId, sellerId, otp)
        );
    }
    @GetMapping("/{orderId}/download-receipt")
    public ResponseEntity<?> downloadReceipt(@PathVariable Long orderId,
                                             @RequestParam Long buyerId) {

       orderHistoryService.validateSellerCanDownload(orderId, buyerId);
        byte[] pdf = orderHistoryService.generateReceipt(orderId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=receipt_" + orderId + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }


}
