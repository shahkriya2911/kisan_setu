package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.OrderStatus;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByStatusAndConfirmationDeadlineBefore(OrderStatus orderStatus, LocalDateTime time);

    boolean existsByAcceptBid(Bid bid);

    boolean existsByListingAndBuyer(Listing listing, User buyer);
//    long countDistinctBuyers();
}
