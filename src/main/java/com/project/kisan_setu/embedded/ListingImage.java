package com.project.kisan_setu.embedded;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListingImage {
    private String fileName;
    private String filePath;
    private String fileType;
    private Boolean isPrimary;
}
