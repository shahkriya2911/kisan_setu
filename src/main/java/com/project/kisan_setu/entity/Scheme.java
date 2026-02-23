package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity //table creation
@Table(name = "schemes") //table name
@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by JPA
@AllArgsConstructor //constructor
public class Scheme {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment
    private Long schemeId;

    //scheme info
    private String schemeTitle;
    private String schemeFullName;
    private String schemeCategory;
    @Column(columnDefinition = "TEXT")
    private String schemeDescription;
    @ElementCollection
    @CollectionTable(
            name = "scheme_benefits",
            joinColumns = @JoinColumn(name = "scheme_id")
    )
    @Column(name = "benefit")
    private List<String> schemeBenefits;
    private String schemeEligibility;
    private String schemeState;
    private String schemeOfficialLink;
    private LocalDateTime schemeLastUpdatedDate;
    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.schemeLastUpdatedDate = LocalDateTime.now();
    }
}
