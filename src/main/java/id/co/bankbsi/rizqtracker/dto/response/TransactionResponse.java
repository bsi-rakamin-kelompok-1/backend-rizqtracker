package id.co.bankbsi.rizqtracker.dto.response;

import id.co.bankbsi.rizqtracker.model.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionResponse extends BaseResponse {
    private PageMetaResponse meta;
    private Object data;

    public void setData(Transaction transaction) {
        this.data = from(transaction);
    }

    public void setData(Page<Transaction> transactions) {
        this.meta = PageMetaResponse.from(transactions);

        this.data = transactions.stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    @Data
    public static class TransactionData {
        private String id;
        private String senderFullName;
        private Long senderAccountNumber;
        private String recipientFullName;
        private Long recipientAccountNumber;
        private String transactionType;
        private String transferCategory;
        private String topupMethod;
        private Long amount;
        private String notes;
        private String referenceNumber;
        private LocalDateTime createdAt;
    }

    private static TransactionData from(Transaction transaction) {
        TransactionData data = new TransactionData();
        data.setId(transaction.getId());
        data.setTransactionType(transaction.getTransactionType().getName());
        data.setSenderFullName(transaction.getSenderAccount().getUser().getFullName());
        data.setSenderAccountNumber(transaction.getSenderAccount().getAccountNumber());

        if (transaction.getRecipientAccount() != null) {
            data.setRecipientFullName(transaction.getRecipientAccount().getUser().getFullName());
            data.setRecipientAccountNumber(transaction.getRecipientAccount().getAccountNumber());
        }

        if (transaction.getTransferCategory() != null) {
            data.setTransferCategory(transaction.getTransferCategory().getName());
        }

        if (transaction.getTopupMethod() != null) {
            data.setTopupMethod(transaction.getTopupMethod().getName());
        }

        data.setAmount(transaction.getAmount());
        data.setNotes(transaction.getNotes());
        data.setReferenceNumber(transaction.getReferenceNumber());
        data.setCreatedAt(transaction.getCreatedAt());

        return data;
    }
}
