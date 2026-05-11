package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByStatusAndConfirmationDeadlineBefore(OrderStatus orderStatus, LocalDateTime time);

    boolean existsByAcceptBid(Bid bid);

    boolean existsByListingAndBuyer(Listing listing, User buyer);

    long countByBuyerUserId(Long userId);

    boolean existsByListing_ListingIdAndStatus(Long listingId, OrderStatus status);

    long countByBuyerUserIdAndStatus(Long userId, OrderStatus orderStatus);

    long countByBuyer_UserIdAndStatus(Long userId, OrderStatus orderStatus);

    @Query("""
            SELECT COALESCE(SUM(o.amount), 0)
            FROM Order o
            WHERE o.seller.userId = :sellerId
            AND o.status IN :statuses
            """)
    BigDecimal sumAmountBySellerUserIdAndStatusIn(
            @Param("sellerId") Long sellerId,
            @Param("statuses") List<OrderStatus> statuses);

    Order findByListingListingId(Long listingId);

    void deleteByListingListingId(Long listingId);

    List<Order> findByBuyer_UserIdOrSeller_UserId(Long userId, Long userId1);

    List<Order> findByBuyer_UserId(Long userId);

    List<Order> findBySeller_UserId(Long userId);

    Optional<Order> findByAcceptBid(Bid bid);

//    long countDistinctBuyers();
}
