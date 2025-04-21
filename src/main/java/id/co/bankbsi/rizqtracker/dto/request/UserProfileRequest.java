package id.co.bankbsi.rizqtracker.dto.request;

import id.co.bankbsi.rizqtracker.validation.ValidPassword;
import id.co.bankbsi.rizqtracker.validation.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileRequest {
    @Email(message = "Email is not valid")
    private String email;

    private String fullName;

    @ValidPhoneNumber(message = "Phone number must be valid with country code (e.g. 6281234567890)")
    private String phoneNumber;
}
