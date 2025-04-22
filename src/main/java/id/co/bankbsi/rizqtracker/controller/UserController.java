package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.request.SetPinRequest;
import id.co.bankbsi.rizqtracker.dto.request.UserProfileRequest;
import id.co.bankbsi.rizqtracker.dto.response.BaseResponse;
import id.co.bankbsi.rizqtracker.dto.response.UserDetailResponse;
import id.co.bankbsi.rizqtracker.service.UserService;
import id.co.bankbsi.rizqtracker.util.SecurityUtility;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityUtility securityUtility;

    @PostMapping("/set-pin")
    public ResponseEntity<BaseResponse> setPin(@Valid @RequestBody SetPinRequest request) {
        BaseResponse response = this.userService.setPin(
                securityUtility.getCurrentUserId(),
                request
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<UserDetailResponse> getUserProfile() {
        UserDetailResponse response = this.userService
                .getCurrentUserDetails(this.securityUtility.getCurrentUserId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/detail")
    public ResponseEntity<UserDetailResponse> updateUserProfile(@Valid @RequestBody UserProfileRequest req) {
        UserDetailResponse response = this.userService
                .updateUserDetails(this.securityUtility.getCurrentUserId(), req);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
