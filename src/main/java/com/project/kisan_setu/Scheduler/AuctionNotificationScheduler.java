package com.project.kisan_setu.Scheduler;

import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionNotificationScheduler {

    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 60000) //
    public void sendAuctionNotification() {

        LocalDateTime now = LocalDateTime.now();

        // Get all active listings
        List<Listing> activeListings =
                listingRepository.findByStatus(AuctionStatus.ACTIVE);

        for (Listing listing : activeListings) {
            if (listing.getAuctionEndTime() == null) {
                continue;
            }
            if (listing.getAuctionEndTime().isBefore(now)) {
                listing.setStatus(AuctionStatus.CLOSED);
                listingRepository.save(listing);
                continue;
            }
            Duration duration = Duration.between(now, listing.getAuctionEndTime());

            long minutesLeft =
                    Duration.between(now, listing.getAuctionEndTime()).toMinutes();

            long hoursLeft =
                    Duration.between(now, listing.getAuctionEndTime()).toHours();

            if (hoursLeft == 24 && !listing.isOneDayNotified()) {

                sendBuyerNotifications(listing, " Only 24 hours left! Place your bids now."
                );

                listing.setOneDayNotified(true);
                listingRepository.save(listing);
            }
            if (minutesLeft == 30 && !listing.isThirtyMinuteNotified()) {

                sendBuyerNotifications(listing, " Only 30 minutes left! Final chance to bid."
                );

                listing.setThirtyMinuteNotified(true);
                listingRepository.save(listing);
            }
        }
    }
    private void sendBuyerNotifications(Listing listing, String message) {

        List<Bid> bids = bidRepository.findByListing(listing);

        Set<User> buyers = bids.stream()
                .map(Bid::getBuyer)
                .collect(Collectors.toSet());

        for (User buyer : buyers) {
            notificationService.notifyUser(buyer, message);
        }
    }
}