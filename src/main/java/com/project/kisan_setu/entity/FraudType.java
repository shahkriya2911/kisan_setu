package com.project.kisan_setu.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fraud_type_master")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeName;

    private Boolean isActive;
}
