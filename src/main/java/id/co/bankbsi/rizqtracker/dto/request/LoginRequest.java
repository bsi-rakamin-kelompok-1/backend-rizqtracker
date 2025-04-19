package id.co.bankbsi.rizqtracker.dto.request;

import id.co.bankbsi.rizqtracker.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "Password is required")
    @ValidPassword
    private String password;
}
