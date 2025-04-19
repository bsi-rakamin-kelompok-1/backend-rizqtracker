package id.co.bankbsi.rizqtracker.service;

import id.co.bankbsi.rizqtracker.model.TransactionType;
import id.co.bankbsi.rizqtracker.repository.TransactionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionTypeService {
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    public List<TransactionType> getAllTransactionTypes() {
        return this.transactionTypeRepository.findAll();
    }
}
