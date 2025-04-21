package id.co.bankbsi.rizqtracker.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserAccountResponse extends BaseResponse {
    private Detail data;

    @Data
    public static class Detail {
        private String fullName;
        private Long accountNumber;
    }
}
