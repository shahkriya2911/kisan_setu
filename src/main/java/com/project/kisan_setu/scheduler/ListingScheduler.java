package com.project.kisan_setu.scheduler;

import com.project.kisan_setu.service.BuyerService;
import com.project.kisan_setu.service.ListingService;
import org.springframework.scheduling.annotation.Scheduled;

public class ListingScheduler {
    private final BuyerService buyerService;

    public ListingScheduler(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    // Runs every 1 minute
    @Scheduled(fixedRate = 15000)
    public void closeExpiredListingsJob() {
        buyerService.closeExpiredListings();
    }
}
