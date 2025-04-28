package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/users/avatar")
public class FileController {

    private static final Map<String, String> MEDIA_TYPES = new HashMap<>();
    static {
        MEDIA_TYPES.put("jpg", "image/jpeg");
        MEDIA_TYPES.put("jpeg", "image/jpeg");
        MEDIA_TYPES.put("png", "image/png");
    }

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        try {
            Path filePath = fileStorageService.getFileStoragePath().resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                String contentType = determineContentType(fileName);
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private String determineContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return MEDIA_TYPES.getOrDefault(extension, "application/octet-stream");
    }
}