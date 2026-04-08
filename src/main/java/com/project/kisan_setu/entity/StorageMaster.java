package com.project.kisan_setu.entity;
import jakarta.persistence.*;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "storage_master")
public class StorageMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storageId;
    private String storageType;
}