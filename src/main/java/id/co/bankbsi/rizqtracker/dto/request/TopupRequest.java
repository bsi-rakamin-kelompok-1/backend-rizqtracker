package id.co.bankbsi.rizqtracker.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TopupRequest {
    @NotBlank(message = "Topup method is required")
    private String topupMethod;

    @Min(value = 1, message = "Amount must be greater than 0")
    private Long amount;

    @Size(max = 25, message = "Notes must be less than 25 characters")
    private String notes;
}
