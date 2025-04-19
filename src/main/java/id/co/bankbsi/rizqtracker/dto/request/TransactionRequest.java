package id.co.bankbsi.rizqtracker.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransactionRequest {
//    @NotBlank(message = "Transaction type ID is required")
//    private Short transactionTypeId;

    @NotBlank(message = "Sender account number is required")
    private Long senderAccountNumber;

    @NotBlank(message = "Recipient account number is required")
    private Long recipientAccountNumber;

//    @NotBlank(message = "Transfer category ID is required")
    private String transferCategory;

//    @NotBlank(message = "Topup method ID is required")
    private String topupMethod;

    @Min(value = 1, message = "Amount must be greater than 0")
    private Long amount;

    private String notes;
}
