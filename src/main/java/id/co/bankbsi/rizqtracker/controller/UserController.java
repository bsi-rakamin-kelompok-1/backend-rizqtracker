package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.response.UserDetailResponse;
import id.co.bankbsi.rizqtracker.service.UserService;
import id.co.bankbsi.rizqtracker.util.SecurityUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityUtility securityUtility;

    @GetMapping("/detail")
    public ResponseEntity<UserDetailResponse> getUserProfile() {
        UserDetailResponse response = this.userService
                .getCurrentUserDetails(this.securityUtility.getCurrentUserId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
