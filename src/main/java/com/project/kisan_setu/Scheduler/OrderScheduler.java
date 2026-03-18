package com.project.kisan_setu.Scheduler;

import com.project.kisan_setu.enums.OrderStatus;
import com.project.kisan_setu.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderScheduler {
    private final OrderService orderService;

    @Scheduled(fixedRate = 60000)
    public void expireOrders(){
        orderService.expirePendingOrders();
    }
}
