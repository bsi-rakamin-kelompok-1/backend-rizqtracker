package id.co.bankbsi.rizqtracker.dto.response;

import id.co.bankbsi.rizqtracker.model.Account;
import id.co.bankbsi.rizqtracker.model.User;
import lombok.Data;

@Data
public class UserRegistrationResult {
    private User user;
    private Account account;

    public static UserRegistrationResult from(User user, Account account) {
        UserRegistrationResult result = new UserRegistrationResult();
        result.setUser(user);
        result.setAccount(account);

        return result;
    }
}
