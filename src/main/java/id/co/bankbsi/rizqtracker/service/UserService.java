package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.repository.AccountRepository;
import id.co.bankbsi.rizqtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;
}
