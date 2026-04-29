package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.ResponseDto.OrderChangeEventResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.OrderResponseDto;
import com.project.kisan_setu.dto.PartialLotRequestDto;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.BidStatus;
import com.project.kisan_setu.enums.SaleType;
import com.project.kisan_setu.service.ListingService;
import com.project.kisan_setu.service.OrderService;
import com.project.kisan_setu.util.ValidatorMethods;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;


@RestController
@RequestMapping("api/orders")
@Tag(name = "Order Management",description = "Endpoints for order related resources")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final ValidatorMethods validatorMethods;
    private final SimpMessagingTemplate messagingTemplate;
    private final ListingService listingService;

    public OrderController(OrderService orderService, ValidatorMethods validatorMethods, SimpMessagingTemplate messagingTemplate, ListingService listingService) {
        this.orderService = orderService;
        this.validatorMethods = validatorMethods;
        this.messagingTemplate = messagingTemplate;
        this.listingService = listingService;
    }

    // accept bid
    @PostMapping("/accept/{bidId}")
    @Operation(summary = "Accept bid method",description = "Seller will accept buyer's bid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "Bid accepted successfully"),
            @ApiResponse(responseCode = "404",description = "Bid not found"),
            @ApiResponse(responseCode = "500",description = "Something went wrong"),
            @ApiResponse(responseCode = "401",description = "Unauthorized user"),
            @ApiResponse(responseCode = "400",description = "Bad input data")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<OrderResponseDto> createBid(
            @Parameter(description = "Bid ID request",required = true)
            @PathVariable Long bidId){
        logger.debug("Create order attempt for bid with id : {}",bidId);
        logger.info("Order created for bid with id : {}",bidId);
        OrderResponseDto response = orderService.createOrderFromAcceptedBid(bidId);
        messagingTemplate.convertAndSend("/topic/auctions"
                ,new OrderChangeEventResponseDto(
                        response.getListingId(),SaleType.AUCTION,
                                AuctionStatus.PENDING,BidStatus.ACCEPTED,
                                null,null,response));
        messagingTemplate.convertAndSend("/topic/auctions/"+response.getListingId()
                ,new OrderChangeEventResponseDto(
                        response.getListingId(),SaleType.AUCTION,
                                AuctionStatus.PENDING,BidStatus.ACCEPTED,
                                null,null,response));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // buyer will confirm seller accept
    @PostMapping("/{orderId}/confirm")
    @Operation(summary = "Confirm seller accept method",
            description = "Buyer will confirm seller's bid accept")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Confirm seller accept success"),
            @ApiResponse(responseCode = "404",description = "Order not found"),
            @ApiResponse(responseCode = "500",description = "Something went wrong"),
            @ApiResponse(responseCode = "400",description = "Bad input data"),
            @ApiResponse(responseCode = "401",description = "Unauthorized user")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<OrderResponseDto> confirmOrder(
            @Parameter(description = "Order ID request",required = true)
            @PathVariable Long orderId) {

        Long buyerId = validatorMethods.getCurrentUserId();
        OrderResponseDto response = orderService.confirmOrder(orderId, buyerId);
        return ResponseEntity.ok(response);
    }

    //buyer will reject accept
    @PostMapping("/{orderId}/reject")
    @Operation(summary = "Reject order method",description = "Buyer will reject seller's bid accept")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "order reject success"),
            @ApiResponse(responseCode = "404",description = "order not found"),
            @ApiResponse(responseCode = "500",description = "something went wrong"),
            @ApiResponse(responseCode = "401",description = "Unauthorized user"),
            @ApiResponse(responseCode = "400",description = "Bad input data")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<OrderResponseDto> rejectOrder(
            @Parameter(description = "order ID request",required = true)
            @PathVariable Long orderId){
        logger.debug("Reject Order attempt for order with id : {}",orderId);
        logger.info("Order rejected for order with id : {}",orderId);
        OrderResponseDto response = orderService.rejectOrder(orderId);
        messagingTemplate.convertAndSend("/topic/auctions",
                new OrderChangeEventResponseDto(null,SaleType.AUCTION,
                        AuctionStatus.ACTIVE,
                        BidStatus.REJECTED,null,null,
                        response));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    @Operation(description = "User can fetch a particular order",summary = "Get order method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "order fetched successfully"),
            @ApiResponse(responseCode = "404",description = "order not found"),
            @ApiResponse(responseCode = "401",description = "Unauthorized user"),
            @ApiResponse(responseCode = "500",description = "Something went wrong"),
            @ApiResponse(responseCode = "400",description = "Bad input data")
    })
    @SecurityRequirement(name = "cookieAuth")
    public Order getOrder(
            @Parameter(description = "order ID request",required = true)
            @PathVariable Long orderId){
        return orderService.getOrder(orderId);
    }

    @PostMapping("/buy-partial/{listingId}")
    @Operation(summary = "Buy partial fixed listing method",description = "Buyer can buy partial fixed listing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400",description = "Bad data input"),
            @ApiResponse(responseCode = "500",description = "Something went wrong"),
            @ApiResponse(responseCode = "401",description = "Unauthorized user"),
            @ApiResponse(responseCode = "201",description = "order created successfully"),
            @ApiResponse(responseCode = "404",description = "Listing not found")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<OrderResponseDto> partialLot(
            @Parameter(description = "Listing ID request",required = true)
            @PathVariable Long listingId,
            @Parameter(description = "buying details")
            @RequestBody PartialLotRequestDto requestDto) {

        OrderResponseDto response = orderService.partialLot(listingId, requestDto);
        ListingResponseDto listing = listingService.getListingById(listingId);
        BigDecimal remainingQuantity = listing.getQuantity();
        AuctionStatus auctionStatus = remainingQuantity.compareTo(BigDecimal.ZERO) == 0
                ? AuctionStatus.SOLD
                : AuctionStatus.ACTIVE;
        messagingTemplate.convertAndSend("/topic/fixed",
                new OrderChangeEventResponseDto(listingId, SaleType.FIXED, auctionStatus,
                        null,response.getTotalBasePrice(),remainingQuantity,response));
        messagingTemplate.convertAndSend("/topic/fixed/"+listingId,
                new OrderChangeEventResponseDto(listingId, SaleType.FIXED, auctionStatus,
                        null,response.getTotalBasePrice(),remainingQuantity,null));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/buy-whole/{listingId}")
    @Operation(description = "Buyer will buy whole fixed listing",summary = "Buy whole fixed listing method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "order created successfully"),
            @ApiResponse(responseCode = "400",description = "Bad data input"),
            @ApiResponse(responseCode = "500",description = "something went wrong"),
            @ApiResponse(responseCode = "401",description = "Unauthorized user"),
            @ApiResponse(responseCode = "404",description = "Listing not found")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<OrderResponseDto> wholeLot(
            @Parameter(description = "Listing ID request",required = true)
            @PathVariable Long listingId) {

        OrderResponseDto response = orderService.wholeLot(listingId);
        ListingResponseDto listing = listingService.getListingById(listingId);
        BigDecimal remainingQuantity = listing.getQuantity();
        messagingTemplate.convertAndSend("/topic/fixed",
                new OrderChangeEventResponseDto(listingId,SaleType.FIXED,
                        AuctionStatus.SOLD,null,remainingQuantity,response.getTotalBasePrice(),response));
        messagingTemplate.convertAndSend("/topic/fixed/"+listingId,
                new OrderChangeEventResponseDto(null,SaleType.FIXED,
                        AuctionStatus.SOLD,null,remainingQuantity,response.getTotalBasePrice(),response));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("cancel-order/{orderId}")
    @Operation(description = "Buyer will cancel order",summary = "cancel order method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "order reject success"),
            @ApiResponse(responseCode = "404",description = "order not found"),
            @ApiResponse(responseCode = "500",description = "something went wrong"),
            @ApiResponse(responseCode = "400",description = "Bad input data"),
            @ApiResponse(responseCode = "401",description = "Unauthorized user")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @Parameter(description = "Order ID request",required = true)
            @PathVariable Long orderId){
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }


    @PostMapping("/verify-delivery-otp/{orderId}")
    @Operation(description = "Verify delivery otp",summary = "verify delivery otp method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OTP verification success"),
            @ApiResponse(responseCode = "400",description = "Bad input data"),
            @ApiResponse(responseCode = "500",description = "Something went wrong"),
            @ApiResponse(responseCode = "401",description = "Unauthorized user"),
            @ApiResponse(responseCode = "404",description = "Order not found")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<String> verifyDeliveryOtp(
            @Parameter(description = "Order ID request",required = true)
            @PathVariable Long orderId,
            @Parameter(description = "order details",required = true)
            @RequestBody OrderResponseDto request
    ) {
        String response = orderService.verifyDeliveryOtp(orderId, request.getOtp());
        return ResponseEntity.ok(response);
    }

}
