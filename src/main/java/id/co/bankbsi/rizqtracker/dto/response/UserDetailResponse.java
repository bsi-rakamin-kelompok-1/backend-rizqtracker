package id.co.bankbsi.rizqtracker.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDetailResponse extends BaseResponse {
    private UserDetail data;

    @Data
    public static class UserAccount {
        private Long accountNumber;
        private Long balance;
    }

    @Data
    public static class UserDetail {
        private String email;
        private String fullName;
        private String phoneNumber;
        private String avatarUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private UserAccount account;
    }
}
