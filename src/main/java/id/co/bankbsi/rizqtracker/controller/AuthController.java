package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.request.LoginRequest;
import id.co.bankbsi.rizqtracker.dto.request.RegisterRequest;
import id.co.bankbsi.rizqtracker.dto.response.LoginResponse;
import id.co.bankbsi.rizqtracker.dto.response.RegisterResponse;
import id.co.bankbsi.rizqtracker.model.User;
import id.co.bankbsi.rizqtracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        User createdUser = this.userService.register(req);
        RegisterResponse registerResponse = RegisterResponse.fromUser(createdUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse loginResponse = new LoginResponse();

        String token = this.userService.login(req);

        loginResponse.setSuccess(true);
        loginResponse.setMessage("Login success");
        loginResponse.setToken(token);

        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }
}
