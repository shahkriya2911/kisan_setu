package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByStatusAndConfirmationDeadlineBefore(OrderStatus orderStatus, LocalDateTime time);

    boolean existsByAcceptBid(Bid bid);

    boolean existsByListingAndBuyer(Listing listing, User buyer);

    long countByBuyerUserId(Long userId);

    boolean existsByListing_ListingIdAndStatus(Long listingId, OrderStatus status);

    long countByBuyerUserIdAndStatus(Long userId, OrderStatus orderStatus);

    Order findByListingListingId(Long listingId);

    List<Order> findByBuyer_UserIdOrSeller_UserId(Long userId, Long userId1);

    List<Order> findByBuyer_UserId(Long userId);

    List<Order> findBySeller_UserId(Long userId);

//    long countDistinctBuyers();
}
