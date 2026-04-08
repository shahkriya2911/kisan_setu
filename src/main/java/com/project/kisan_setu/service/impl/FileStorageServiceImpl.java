package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path basePath;
    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    public FileStorageServiceImpl() {
        this.basePath = Paths.get("api").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.basePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String folderName) {
        logger.info("Storing file in folder path...");
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String fileName = UUID.randomUUID() + extension;

        try {
            Path folderPath = basePath.resolve(folderName);
            Files.createDirectories(folderPath);

            Path targetLocation = folderPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation,
                    StandardCopyOption.REPLACE_EXISTING);

            // Return relative path for DB
            logger.info("File stored success...");
            return folderName + "/" + fileName;
        } catch (IOException e) {
            logger.error("File stored failure...");
            throw new RuntimeException("File upload failed", e);
        }
    }
}