package com.project.kisan_setu.repository;

import com.project.kisan_setu.entity.Notification;
import com.project.kisan_setu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    @Modifying
    @Query("""
UPDATE Notification n
SET n.actionCompleted = true
WHERE n.order.id = :orderId
AND n.type = 'BID_ACCEPTED'
""")
    void markOrderNotificationHandled(Long orderId);
}
