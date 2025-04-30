package id.co.bankbsi.rizqtracker.dto.response;

import id.co.bankbsi.rizqtracker.model.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransferResponse extends BaseResponse {
    private TransferData data;

    @Data
    public static class TransferData {
        private String id;
        private String transactionType;
        private Long senderAccountNumber;
        private Long recipientAccountNumber;
        private String recipientFullName;
        private String transferCategory;
        private Long amount;
        private String notes;
        private String referenceNumber;
        private LocalDateTime createdAt;
    }

    public static TransferResponse from(Transaction transaction) {
        TransferResponse response = new TransferResponse();
        TransferData data = new TransferData();

        data.setId(transaction.getId());
        data.setTransactionType(transaction.getTransactionType().getName());
        data.setSenderAccountNumber(transaction.getSenderAccount().getAccountNumber());
        data.setRecipientAccountNumber(transaction.getRecipientAccount().getAccountNumber());
        data.setRecipientFullName(transaction.getRecipientAccount().getUser().getFullName());
        data.setTransferCategory(transaction.getTransferCategory().getName());
        data.setAmount(transaction.getAmount());
        data.setNotes(transaction.getNotes());
        data.setReferenceNumber(transaction.getReferenceNumber());
        data.setCreatedAt(transaction.getCreatedAt());

        response.setSuccess(true);
        response.setMessage("Transfer transaction created successfully");
        response.setData(data);
        return response;
    }
}
