package com.project.kisan_setu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment
    private Long userId;

    //user details
    private String fullName;
    private String email;
    @Column(nullable = false,unique = true)
    private String mobileNumber;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    //relationship with listings (as a seller)
    @OneToMany(mappedBy = "seller")
    private List<Listing> listings = new ArrayList<>();

    //relationship with buying requirement (as a buyer)
    @OneToMany(mappedBy = "buyer")
    private List<BuyingRequirement> buyingRequirements = new ArrayList<>();
}
