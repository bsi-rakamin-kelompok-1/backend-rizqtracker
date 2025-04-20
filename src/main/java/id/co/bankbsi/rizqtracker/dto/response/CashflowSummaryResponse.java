package id.co.bankbsi.rizqtracker.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CashflowSummaryResponse extends BaseCashflowResponse {
    private Summary summary;

    @Data
    public static class Summary {
        private Income income;
        private Expense expense;
    }

    @Data
    public static class Income {
        private Long totalTopup;
        private Long totalTransfer;
    }

    @Data
    public static class Expense {
        private Long totalNeeds;
        private Long totalBills;
        private Long totalShopping;
        private Long totalTransport;
        private Long totalTransferOfWealth;
    }
}