package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.RequestDto.ReportUserRequestDto;
import com.project.kisan_setu.dto.RequestDto.ReviewRequestDto;
import com.project.kisan_setu.dto.ResponseDto.OrderHistoryResponseDto;
import com.project.kisan_setu.service.OrderHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("api/orderHistory")
@RequiredArgsConstructor
@Tag(name = "Order History Management", description = "Endpoints for order history related resources")
public class OrderHistoryController {
    private final OrderHistoryService orderHistoryService;

    @GetMapping
    @Operation(summary = "Get all order history method", description = "Used by user to get complete order history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order history fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<OrderHistoryResponseDto>> getAllOrderHistory() {
        return ResponseEntity.ok(orderHistoryService.getAllOrderHistory());
    }

    @GetMapping("/purchased")
    @Operation(summary = "Get purchased order history method", description = "Used by user to get purchased order history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchased order history fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<OrderHistoryResponseDto>> getPurchasedOrderHistory() {
        return ResponseEntity.ok(orderHistoryService.getPurchasedOrderHistory());
    }

    @GetMapping("/sold")
    @Operation(summary = "Get sold order history method", description = "Used by user to get sold order history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sold order history fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<OrderHistoryResponseDto>> getSoldOrderHistory() {
        return ResponseEntity.ok(orderHistoryService.getSoldOrderHistory());
    }

    @PostMapping("/seller/{orderId}")
    @Operation(summary = "Review seller method", description = "Used by buyer to submit a seller review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seller review submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid review details"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<String> reviewSeller(
            @Parameter(description = "Order ID request", required = true)
            @PathVariable Long orderId,
            @Parameter(description = "Seller review details", required = true)
            @RequestBody ReviewRequestDto requestDto) {

        return ResponseEntity.ok(
                orderHistoryService.submitSellerReview(orderId, requestDto)
        );
    }

    @PostMapping("/buyer/{orderId}")
    @Operation(summary = "Review buyer method", description = "Used by seller to submit a buyer review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Buyer review submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid review details"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<String> reviewBuyer(
            @Parameter(description = "Order ID request", required = true)
            @PathVariable Long orderId,
            @Parameter(description = "Buyer review details", required = true)
            @RequestBody ReviewRequestDto requestDto) {

        return ResponseEntity.ok(
               orderHistoryService.submitBuyerReview(orderId, requestDto)
        );
    }

    @PostMapping("/report-Seller/{orderId}")
    @Operation(summary = "Report seller method", description = "Used by buyer to report a seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seller reported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid report details"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<String> reportSeller(
            @Parameter(description = "Order ID request", required = true)
            @PathVariable Long orderId,
            @Parameter(description = "Seller report details", required = true)
            @RequestBody ReportUserRequestDto requestDto) {

        return ResponseEntity.ok(orderHistoryService.reportSeller(orderId, requestDto));
    }

    @PostMapping("/report-Buyer/{orderId}")
    @Operation(summary = "Report buyer method", description = "Used by seller to report a buyer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Buyer reported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid report details"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<String> reportBuyer(
            @Parameter(description = "Order ID request", required = true)
            @PathVariable Long orderId,
            @Parameter(description = "Buyer report details", required = true)
            @RequestBody ReportUserRequestDto requestDto) {

        return ResponseEntity.ok(orderHistoryService.reportBuyer(orderId, requestDto));
    }

    @GetMapping("/{orderId}/download-invoice")
    @Operation(summary = "Download invoice method", description = "Used by user to download invoice for an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long orderId) {
        byte[] pdf = orderHistoryService.downloadInvoice(orderId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=invoice_" + orderId + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }


}
