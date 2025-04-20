package id.co.bankbsi.rizqtracker.repository;

import id.co.bankbsi.rizqtracker.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Page<Transaction> findAllBySenderAccount_User_Id(Integer userId, Pageable pageable);

    List<Transaction> findByTransactionType_NameAndSenderAccount_User_IdAndCreatedAtBetween(
            String transactionTypeName, Integer userId, LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByTransactionType_NameAndRecipientAccount_User_IdAndCreatedAtBetween(
            String transactionTypeName, Integer userId, LocalDateTime startDate, LocalDateTime endDate);
}
