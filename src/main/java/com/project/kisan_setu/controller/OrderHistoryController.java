package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.ReportUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.ReviewRequestDto;
import com.project.kisan_setu.dto.ResponseDto.OrderHistoryResponseDto;
import com.project.kisan_setu.service.OrderHistoryService;
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

    @PostMapping("/seller/{orderId}")
    public ResponseEntity<String> reviewSeller(
            @PathVariable Long orderId,
            @RequestBody ReviewRequestDto requestDto) {

        return ResponseEntity.ok(
                orderHistoryService.submitSellerReview(orderId, requestDto)
        );
    }
    @PostMapping("/buyer/{orderId}")
    public ResponseEntity<String> reviewBuyer(
            @PathVariable Long orderId,
            @RequestBody ReviewRequestDto requestDto) {

        return ResponseEntity.ok(
               orderHistoryService.submitBuyerReview(orderId, requestDto)
        );
    }

    @PostMapping("/report-Seller/{orderId}")
    public ResponseEntity<String> reportSeller(
            @PathVariable Long orderId,
            @RequestBody ReportUserRequestDto requestDto) {

        return ResponseEntity.ok(orderHistoryService.reportSeller(orderId, requestDto));
    }
    @PostMapping("/report-Buyer/{orderId}")
    public ResponseEntity<String> reportBuyer(
            @PathVariable Long orderId,
            @RequestBody ReportUserRequestDto requestDto) {

        return ResponseEntity.ok(orderHistoryService.reportBuyer(orderId, requestDto));
    }

    @GetMapping({"/{orderId}/download-invoice"} /*"/{orderId}/download-receipt"}*/)
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long orderId) {
        byte[] pdf = orderHistoryService.downloadInvoice(orderId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=invoice_" + orderId + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }


}
