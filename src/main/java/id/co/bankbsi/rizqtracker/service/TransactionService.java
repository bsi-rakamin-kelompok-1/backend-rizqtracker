package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.dto.request.TopupRequest;
import id.co.bankbsi.rizqtracker.dto.request.TransferRequest;
import id.co.bankbsi.rizqtracker.exception.InsufficientBalanceException;
import id.co.bankbsi.rizqtracker.exception.ResourceNotFoundException;
import id.co.bankbsi.rizqtracker.model.*;
import id.co.bankbsi.rizqtracker.repository.*;
import id.co.bankbsi.rizqtracker.util.ReferenceNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TransactionService {
    private static final String TRANSFER = "transfer";
    private static final String TOPUP = "topup";

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

    @Transactional
    public Transaction createTransfer(TransferRequest req, Integer userId) {
        TransactionType type = this.transactionTypeRepository.findByName(TRANSFER)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction type not found"));

        Account sender = this.accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));

        Account recipient = this.accountRepository.findByAccountNumber(req.getRecipientAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient account not found"));

        TransferCategory category = this.transferCategoryRepository.findByName(req.getTransferCategory())
                .orElseThrow(() -> new ResourceNotFoundException("Transfer category not found"));

        if (Objects.equals(sender.getAccountNumber(), recipient.getAccountNumber())) {
            throw new IllegalArgumentException("Sender and recipient accounts cannot be the same");
        }

        if (sender.getBalance() < req.getAmount()) {
            throw new InsufficientBalanceException("Insufficient balance for transfer");
        }

        String referenceNumber = ReferenceNumberGenerator.generate(type.getName());

        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setSenderAccount(sender);
        transaction.setRecipientAccount(recipient);
        transaction.setTransferCategory(category);
        transaction.setAmount(req.getAmount());
        transaction.setNotes(req.getNotes());
        transaction.setReferenceNumber(referenceNumber);
        
        // Update balance
        sender.setBalance(sender.getBalance() - req.getAmount());
        recipient.setBalance(recipient.getBalance() + req.getAmount());

        // Save transaction first
        Transaction savedTransaction = this.transactionRepository.save(transaction);

        // Save updated account balances
        this.accountRepository.saveAll(List.of(sender, recipient));

        return savedTransaction;
    }

    @Transactional
    public Transaction createTopup(TopupRequest req, Integer userId) {
        TransactionType type = this.transactionTypeRepository.findByName(TOPUP)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction type not found"));

        Account sender = this.accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));

        TopupMethod method = this.topupMethodRepository.findByName(req.getTopupMethod())
                .orElseThrow(() -> new ResourceNotFoundException("Topup method not found"));

        String referenceNumber = ReferenceNumberGenerator.generate(type.getName());

        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setSenderAccount(sender);
        transaction.setTopupMethod(method);
        transaction.setAmount(req.getAmount());
        transaction.setReferenceNumber(referenceNumber);

        // Update balance
        sender.setBalance(sender.getBalance() + req.getAmount());

        // Save transaction first
        Transaction savedTransaction = this.transactionRepository.save(transaction);

        // Save account after
        this.accountRepository.save(sender);

        return savedTransaction;
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return this.transactionRepository.findAllBySenderAccount_User_Id(userId);
    }
}
