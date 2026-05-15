package com.project.kisan_setu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "changing_languages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangingLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long changingLanguageId;
    private String displayLanguage;
    private String region;
    private String timeZone;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
