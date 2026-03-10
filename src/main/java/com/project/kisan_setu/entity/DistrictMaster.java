package com.project.kisan_setu.entity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "district_master")
public class DistrictMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long districtId;
    private String name;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private StateMaster state;



}