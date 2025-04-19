package id.co.bankbsi.rizqtracker.dto.response;

import id.co.bankbsi.rizqtracker.model.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class TopupResponse extends BaseResponse {
    private TopupData data;

    @Data
    public static class TopupData {
        private String id;
        private String transactionType;
        private Long senderAccountNumber;
        private String topupMethod;
        private Long amount;
        private String referenceNumber;
        private LocalDateTime createdAt;
    }

    public static TopupResponse from(Transaction transaction) {
        TopupResponse response = new TopupResponse();
        TopupData data = new TopupData();

        data.setId(transaction.getId());
        data.setTransactionType(transaction.getTransactionType().getName());
        data.setSenderAccountNumber(transaction.getSenderAccount().getAccountNumber());
        data.setTopupMethod(transaction.getTopupMethod().getName());
        data.setAmount(transaction.getAmount());
        data.setReferenceNumber(transaction.getReferenceNumber());
        data.setCreatedAt(transaction.getCreatedAt());

        response.setSuccess(true);
        response.setMessage("Topup transaction created successfully");
        response.setData(data);
        return response;
    }
}
