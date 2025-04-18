package id.co.bankbsi.rizqtracker.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String phoneNumber;
}
