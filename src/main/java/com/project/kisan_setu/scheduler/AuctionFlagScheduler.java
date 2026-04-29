package com.project.kisan_setu.scheduler;

import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.BidStatus;
import com.project.kisan_setu.enums.NotificationStatus;
import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.repository.UserRepository;
import com.project.kisan_setu.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionFlagScheduler {

    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 60000)
    public void autoFlagUsers() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.minusHours(24);

        List<Listing> listings = listingRepository.findAll();

        for (Listing listing : listings) {

            if (listing.getAuctionEndTime() == null) continue;

            if (listing.getAuctionEndTime().isBefore(cutoff)
                    && !listing.isSellerFlagProcessed()) {

                boolean hasAccepted = bidRepository
                        .existsByListingAndBidStatus(listing, BidStatus.ACCEPTED);

                if (!hasAccepted) {

                    User seller = listing.getSeller();

                    seller.setViolationCount(seller.getViolationCount() + 1);
                    userRepository.save(seller);

                    listing.setSellerFlagProcessed(true);
                    listingRepository.save(listing);

                    notificationService.createNotification(
                            seller,
                            "You did not accept/reject bids within 24 hours. Violations: "
                                    + seller.getViolationCount(),
                            NotificationStatus.FLAGGED,
                            listing,
                            null,
                            null
                    );
                }
            }
        }
        List<Bid> bids =
                bidRepository.findByBidStatusAndAcceptedTimeBefore(
                        BidStatus.ACCEPTED, cutoff
                );

        for (Bid bid : bids) {

            if (bid.getBuyerResponse() == null
                    && !bid.isBuyerFlagProcessed()) {

                User buyer = bid.getBuyer();

                buyer.setViolationCount(buyer.getViolationCount() + 1);
                userRepository.save(buyer);

                bid.setBuyerFlagProcessed(true);
                bidRepository.save(bid);

                notificationService.createNotification(
                        buyer,
                        "You did not respond within 24 hours. Violations: "
                                + buyer.getViolationCount(),
                        NotificationStatus.FLAGGED,
                        bid.getListing(),
                        bid,
                        null
                );
            }
        }
    }
}