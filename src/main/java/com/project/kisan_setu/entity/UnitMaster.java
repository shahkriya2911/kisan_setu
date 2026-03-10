package com.project.kisan_setu.entity;
import jakarta.persistence.*;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "unit_master")
public class UnitMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long unitId;

    private String unitName;
}