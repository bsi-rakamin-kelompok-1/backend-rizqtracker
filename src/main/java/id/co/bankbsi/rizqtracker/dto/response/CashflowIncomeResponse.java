package id.co.bankbsi.rizqtracker.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CashflowIncomeResponse extends BaseCashflowResponse {
    private IncomeDetails incomeDetails;

    @Data
    public static class IncomeDetails {
        private List<TopupData> topupData;
        private List<TransferData> transferData;
    }

    @Data
    public static class TopupData {
        private String transactionId;
        private String topupMethod;
        private Long amount;
        private String notes;
        private LocalDateTime createdAt;
    }

    @Data
    public static class TransferData {
        private String transactionId;
        private String transactionCategory;
        private String senderFullName;
        private Long senderAccountNumber;
        private Long amount;
        private String notes;
        private LocalDateTime createdAt;
    }

}
