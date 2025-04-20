package id.co.bankbsi.rizqtracker.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseCashflowResponse extends BaseResponse {
    private Period period;

    @Data
    public static class Period {
        private LocalDateTime start;
        private LocalDateTime end;

        public static Period from(LocalDateTime startDate, LocalDateTime endDate) {
            Period period = new Period();
            period.setStart(startDate);
            period.setEnd(endDate);
            return period;
        }
    }
}
