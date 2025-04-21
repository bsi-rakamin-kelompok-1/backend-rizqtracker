package id.co.bankbsi.rizqtracker.controller;

import id.co.bankbsi.rizqtracker.dto.response.UserAccountResponse;
import id.co.bankbsi.rizqtracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/{accountNumber}")
    public ResponseEntity<UserAccountResponse> checkAccountNumber(@PathVariable("accountNumber") Long accountNumber) {
        UserAccountResponse response = this.accountService.checkAccountExists(accountNumber);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // TODO: Get top 5 recent recipient accounts
}
