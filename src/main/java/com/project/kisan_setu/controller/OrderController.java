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

    // accept bid
    @PostMapping("/accept/{bidId}")
    public ResponseEntity<?> createBid(@PathVariable Long bidId){
        logger.debug("Create order attempt for bid with id : {}",bidId);
        logger.info("Order created for bid with id : {}",bidId);
        return ResponseEntity.ok(orderService.createOrderFromAcceptedBid(bidId));
    }

    // buyer will confirm seller accept
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponseDto> confirmOrder(
            @PathVariable Long orderId) {

        Long buyerId = validatorMethods.getCurrentUserId();

        OrderResponseDto response = orderService.confirmOrder(orderId, buyerId);
        return ResponseEntity.ok(response);
    }

    //buyer will reject accept
    @PostMapping("/{orderId}/reject")
    public ResponseEntity<?> rejectOrder(@PathVariable Long orderId){
        logger.debug("Reject Order attempt for order with id : {}",orderId);
        logger.info("Order rejected for order with id : {}",orderId);
        orderService.rejectOrder(orderId);
        return ResponseEntity.ok("Buyer rejected accepted bid");
    }


    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable Long orderId){
        return orderService.getOrder(orderId);
    }

    @PostMapping("/buy-partial/{listingId}")
    public ResponseEntity<OrderResponseDto> partialLot(
            @PathVariable Long listingId,
            @RequestBody PartialLotRequestDto requestDto) {

        OrderResponseDto response = orderService.partialLot(listingId, requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/buy-whole/{listingId}")
    public ResponseEntity<OrderResponseDto> wholeLot(
            @PathVariable Long listingId) {

        OrderResponseDto response = orderService.wholeLot(listingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("cancel-order/{orderId}")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable Long orderId){
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }


    @PostMapping("/{orderId}/out-for-delivery")
    public ResponseEntity<OrderResponseDto> markOutForDelivery(@PathVariable Long orderId){
        return ResponseEntity.ok(orderService.markOutForDelivery(orderId));
    }

}
