package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.response.BaseResponse;
import id.co.bankbsi.rizqtracker.dto.response.DataResponse;
import id.co.bankbsi.rizqtracker.exception.FileStorageException;
import id.co.bankbsi.rizqtracker.model.User;
import id.co.bankbsi.rizqtracker.repository.UserRepository;
import id.co.bankbsi.rizqtracker.service.FileStorageService;
import id.co.bankbsi.rizqtracker.util.SecurityUtility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/users/avatar")
public class UserAvatarController {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityUtility securityUtility;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(@RequestParam("avatar") MultipartFile file, HttpServletRequest request) {
        // Get current user ID
        Integer userId = securityUtility.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse(false, "User not authenticated"));
        }

        // Check if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse(false, "User not found"));
        }
        
        User user = userOptional.get();

        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new BaseResponse(false, "Avatar file is required"));
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest()
                    .body(new BaseResponse(false, "File size exceeds maximum limit of 5MB"));
        }

        // Validate file format
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return ResponseEntity.badRequest()
                    .body(new BaseResponse(false, "Invalid file name"));
        }

        String fileExtension = getFileExtension(fileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            return ResponseEntity.badRequest()
                    .body(new BaseResponse(false, "Only JPG, JPEG, and PNG files are allowed"));
        }

        try {
            // Store file
            String storedFileName = fileStorageService.storeFile(file, fileExtension);
            
            // Create file URL
            String fileDownloadUri = ServletUriComponentsBuilder.fromContextPath(request)
                    .path("/v1/users/avatar/")
                    .path(storedFileName)
                    .toUriString();

            // Update user avatar in database
            user.setAvatarUrl(fileDownloadUri);
            userRepository.save(user);

            return ResponseEntity.ok(new DataResponse<>(true, "Avatar uploaded successfully", fileDownloadUri));
        } catch (FileStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(false, e.getMessage()));
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }
}
