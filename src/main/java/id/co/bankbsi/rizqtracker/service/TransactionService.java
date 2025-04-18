package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    @Autowired
    private TransferCategoryRepository transferCategoryRepository;

    @Autowired
    private TopupMethodRepository topupMethodRepository;
}
