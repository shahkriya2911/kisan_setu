package com.project.kisan_setu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity // table creation
@Table(name = "users") //table name
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String userFullName;

    @Column(nullable = false,unique = true)
    private String userEmail;

    @Column(nullable = false,unique = true)
    private String userPhoneNumber;

    @Column(nullable = false)
    private String userPassword;

    @Column(nullable = false)
    private String userCreatedAt;

    @Column(nullable = false)
    private String userAddress;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JsonIgnore
    private List<UserRole> userRoles;
}
