package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
}
