package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "return_and_shippings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnAndShipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnAndShippingId;
    private String shippingAddress;
    private String returnWindow;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
