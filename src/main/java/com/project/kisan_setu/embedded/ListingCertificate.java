package com.project.kisan_setu.embedded;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListingCertificate {
    private String fileName;
    private String filePath;
    private String fileType;
    private LocalDate issuedDate;
}
