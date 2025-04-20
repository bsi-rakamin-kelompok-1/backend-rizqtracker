package id.co.bankbsi.rizqtracker.dto.response;

import id.co.bankbsi.rizqtracker.model.Account;
import id.co.bankbsi.rizqtracker.model.User;
import lombok.Data;

@Data
public class UserRegistrationResponse {
    private User user;
    private Account account;

    public static UserRegistrationResponse from(User user, Account account) {
        UserRegistrationResponse result = new UserRegistrationResponse();
        result.setUser(user);
        result.setAccount(account);

        return result;
    }
}
