package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.dto.request.TransactionRequest;
import id.co.bankbsi.rizqtracker.exception.InsufficientBalanceException;
import id.co.bankbsi.rizqtracker.exception.ResourceNotFoundException;
import id.co.bankbsi.rizqtracker.model.*;
import id.co.bankbsi.rizqtracker.repository.*;
import id.co.bankbsi.rizqtracker.util.ReferenceNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public Transaction createTransaction(String type, TransactionRequest req) {
        // Check transaction type in parameter ?type=transfer/topup
        if (type == null || this.transactionTypeRepository.findByName(type).isEmpty()) {
            throw new IllegalArgumentException("Transaction type must be either 'transfer' or 'topup'");
        }

        // Process in separate function
        Transaction newTransaction;
        if (type.equals("transfer")) {
            newTransaction = createTransfer(req);
        } else {
            newTransaction = createTopup(req);
        }

        return this.transactionRepository.save(newTransaction);
    }

    private Transaction createTransfer(TransactionRequest req) {
        TransactionType type = this.transactionTypeRepository.findByName("transfer")
                .orElseThrow(() -> new ResourceNotFoundException("Transaction type not found"));

        Account sender = this.accountRepository.findByAccountNumber(req.getSenderAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));

        Account recipient = this.accountRepository.findByAccountNumber(req.getRecipientAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient account not found"));

        TransferCategory category = this.transferCategoryRepository.findByName(req.getTransferCategory())
                .orElseThrow(() -> new ResourceNotFoundException("Transfer category not found"));

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

        return transaction;
    }

    private Transaction createTopup(TransactionRequest req) {
        TransactionType type = this.transactionTypeRepository.findByName("topup")
                .orElseThrow(() -> new ResourceNotFoundException("Transaction type not found"));

        Account sender = this.accountRepository.findByAccountNumber(req.getSenderAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));

        TopupMethod method = this.topupMethodRepository.findByName(req.getTopupMethod())
                .orElseThrow(() -> new ResourceNotFoundException("Topup method not found"));

        String referenceNumber = ReferenceNumberGenerator.generate(type.getName());

        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setSenderAccount(sender);
        transaction.setTopupMethod(method);
        transaction.setAmount(req.getAmount());
        transaction.setNotes(req.getNotes());
        transaction.setReferenceNumber(referenceNumber);

        // Update balance
        sender.setBalance(sender.getBalance() + req.getAmount());

        return transaction;
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return this.transactionRepository.findAllBySenderAccount_User_Id(userId);
    }
}
