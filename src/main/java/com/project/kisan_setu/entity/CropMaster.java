package com.project.kisan_setu.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "crop_master")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CropMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cropId;

    @Column(nullable = false, unique = true)
    private String cropName;
}
