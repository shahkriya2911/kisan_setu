package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
//    long countDistinctBuyers();
}
