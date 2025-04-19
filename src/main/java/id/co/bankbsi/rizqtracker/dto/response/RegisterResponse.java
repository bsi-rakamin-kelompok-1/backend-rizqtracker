package id.co.bankbsi.rizqtracker.dto.response;

import id.co.bankbsi.rizqtracker.model.Account;
import id.co.bankbsi.rizqtracker.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterResponse extends BaseResponse {
    private UserAccountData data;

    @Data
    public static class UserAccountData {
        private String email;
        private String fullName;
        private String phoneNumber;
        private String avatarUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private AccountData account;
    }

    @Data
    public static class AccountData {
        private Long accountNumber;
        private Long balance;
    }

    public static RegisterResponse fromUserAndAccount(User user, Account account) {
        RegisterResponse response = new RegisterResponse();
        response.setSuccess(true);
        response.setMessage("User created successfully");

        AccountData accountData = new AccountData();
        accountData.setAccountNumber(account.getAccountNumber());
        accountData.setBalance(account.getBalance());

        UserAccountData userAccountData = new UserAccountData();
        userAccountData.setEmail(user.getEmail());
        userAccountData.setFullName(user.getFullName());
        userAccountData.setPhoneNumber(user.getPhoneNumber());
        userAccountData.setAvatarUrl(user.getAvatarUrl());
        userAccountData.setCreatedAt(user.getCreatedAt());
        userAccountData.setUpdatedAt(user.getUpdatedAt());
        userAccountData.setAccount(accountData);

        response.setData(userAccountData);

        return response;
    }
}
