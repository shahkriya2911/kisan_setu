package com.project.kisan_setu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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