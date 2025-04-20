package id.co.bankbsi.rizqtracker.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TransferRequest {
    @NotNull(message = "Recipient account number is required")
    private Long recipientAccountNumber;

    @NotBlank(message = "Transfer category is required")
    private String transferCategory;

    @Min(value = 1, message = "Amount must be greater than 0")
    private Long amount;

    @Size(max = 25, message = "Notes must be less than 25 characters")
    private String notes;
}
