package com.project.kisan_setu.entity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "state_master")
public class StateMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stateId;
    private String name;


}