package id.co.bankbsi.rizqtracker.repository;

import id.co.bankbsi.rizqtracker.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionRepositoryCustom {
    Page<Transaction> findTransactionsWithFilters(
            Integer userId,
            String keyword,
            String transactionType,
            String transferCategory,
            String topupMethod,
            Pageable pageable);
}