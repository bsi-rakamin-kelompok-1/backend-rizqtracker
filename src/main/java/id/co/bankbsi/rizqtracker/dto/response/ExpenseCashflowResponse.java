package id.co.bankbsi.rizqtracker.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExpenseCashflowResponse extends BaseCashflowResponse {
    private ExpenseDetails expenseDetails;

    @Data
    public static class ExpenseDetails {
        private List<TransferData> needs;
        private List<TransferData> bills;
        private List<TransferData> shopping;
        private List<TransferData> transport;
        private List<TransferData> transferOfWealth;
    }

    @Data
    public static class TransferData {
        private String transactionId;
        private String recipientFullName;
        private Long recipientAccountNumber;
        private Long amount;
        private String notes;
        private LocalDateTime createdAt;
    }
}
