package com.project.kisan_setu.entity;

import com.project.kisan_setu.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    private String message;
    private Boolean isRead = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationStatus type;

    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @ManyToOne
    @JoinColumn(name = "bid_id")
    private Bid bid;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;


}
