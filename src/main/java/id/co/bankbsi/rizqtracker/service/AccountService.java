package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.dto.response.UserAccountResponse;
import id.co.bankbsi.rizqtracker.exception.ResourceNotFoundException;
import id.co.bankbsi.rizqtracker.model.Account;
import id.co.bankbsi.rizqtracker.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public UserAccountResponse checkAccountExists(Long accountNumber) {
        UserAccountResponse response = new UserAccountResponse();

        Account account = this.accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account number not found"));


        UserAccountResponse.Detail detail = new UserAccountResponse.Detail();
        detail.setFullName(account.getUser().getFullName());
        detail.setAccountNumber(account.getAccountNumber());

        response.setData(detail);
        response.setSuccess(true);
        response.setMessage("Account number found");

        return response;
    }
}
