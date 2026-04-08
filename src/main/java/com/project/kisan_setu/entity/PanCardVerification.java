package com.project.kisan_setu.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "panCard_verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanCardVerification extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long panId;
    private String panNumber;
    private String nameOnPan;
    private String dateOfBirth;
    private String panImagePath;
    private boolean verified = false;

    private LocalDateTime submittedAt = LocalDateTime.now();
    private LocalDateTime verifiedAt;

    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}