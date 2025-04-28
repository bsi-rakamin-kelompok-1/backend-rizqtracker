package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir:uploads/images}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored", ex);
        }
    }

    public String storeFile(MultipartFile file, String extension) {
        // Generate unique file name with UUID
        String fileName = UUID.randomUUID().toString() + "." + extension;

        try {
            // Check and create directory if it doesn't exist
            if (!Files.exists(this.fileStorageLocation)) {
                try {
                    Files.createDirectories(this.fileStorageLocation);
                } catch (IOException e) {
                    throw new FileStorageException("Failed to create storage directory: " + e.getMessage(), e);
                }
            }

            // Verify directory is writable
            if (!Files.isWritable(this.fileStorageLocation)) {
                throw new FileStorageException("Storage directory is not writable: " + this.fileStorageLocation.toString());
            }

            // Copy file to target location
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Reason: " + ex.getMessage(), ex);
        }
    }
    
    public Path getFileStoragePath() {
        return fileStorageLocation;
    }
}