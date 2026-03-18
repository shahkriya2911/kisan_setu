package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.ResponseDto.OrderResponseDto;
import com.project.kisan_setu.dto.PartialLotRequestDto;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.service.OrderService;
import com.project.kisan_setu.util.ValidatorMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final ValidatorMethods validatorMethods;

    public OrderController(OrderService orderService, ValidatorMethods validatorMethods) {
        this.orderService = orderService;
        this.validatorMethods = validatorMethods;
    }

    @PostMapping("/create/{bidId}")
    public ResponseEntity<?> createOrder(@PathVariable Long bidId){
        logger.debug("Create order attempt for bid with id : {}",bidId);
        logger.info("Order created for bid with id : {}",bidId);
        return ResponseEntity.ok(orderService.createOrderFromAcceptedBid(bidId));
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<?> confirmOrder(@PathVariable Long orderId){
        logger.debug("Confirm Order attempt for order with id : {}",orderId);
        logger.info("Order confirmed for order with id : {}",orderId);
        Long buyerId = validatorMethods.getCurrentUserId();
        return ResponseEntity.ok(orderService.confirmOrder(orderId,buyerId));
    }

    @PostMapping("/{orderId}/payment-success")
    public ResponseEntity<?> paymentSuccess(@PathVariable Long orderId){
        logger.debug("Payment Success attempt for order with id : {}",orderId);
        logger.info("Payment success for order with id : {}",orderId);
        return ResponseEntity.ok(orderService.markPaymentSuccess(orderId));
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable Long orderId){
        return orderService.getOrder(orderId);
    }

    @PostMapping("buy-partial/{listingId}")
    public ResponseEntity<OrderResponseDto> buyPartialLot(@PathVariable Long listingId,
                                                          @RequestBody PartialLotRequestDto requestDto){
        return ResponseEntity.ok(orderService.partialLot(listingId,requestDto));
    }

    @PostMapping("buy-whole/{listingId}")
    public ResponseEntity<OrderResponseDto> buyWholeLot(@PathVariable Long listingId){
        return ResponseEntity.ok(orderService.wholeLot(listingId));
    }

    @PostMapping("cancel-order/{orderId}")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable Long orderId){
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

}
