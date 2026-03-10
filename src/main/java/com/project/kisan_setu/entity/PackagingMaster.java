package com.project.kisan_setu.entity;
import jakarta.persistence.*;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "packaging_master")
public class PackagingMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packagingId;
    private String packagingType;
}