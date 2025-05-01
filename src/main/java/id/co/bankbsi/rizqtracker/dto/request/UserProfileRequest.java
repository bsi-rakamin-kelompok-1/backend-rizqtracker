package id.co.bankbsi.rizqtracker.dto.request;

import id.co.bankbsi.rizqtracker.validation.ValidPhoneNumber;
import lombok.Data;

@Data
public class UserProfileRequest {
    private String fullName;

    @ValidPhoneNumber(message = "Phone number must be valid with country code (e.g. 6281234567890)")
    private String phoneNumber;
}
