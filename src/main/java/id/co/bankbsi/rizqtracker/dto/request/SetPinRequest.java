package id.co.bankbsi.rizqtracker.dto.request;

import id.co.bankbsi.rizqtracker.validation.ValidPin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SetPinRequest {
    @NotBlank(message = "PIN is required")
    @ValidPin
    private String pin;

    @NotBlank(message = "Confirm PIN is required")
    @ValidPin
    private String confirmPin;
}
