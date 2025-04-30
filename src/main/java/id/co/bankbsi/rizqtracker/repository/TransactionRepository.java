package id.co.bankbsi.rizqtracker.repository;

import id.co.bankbsi.rizqtracker.model.Account;
import id.co.bankbsi.rizqtracker.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String>, TransactionRepositoryCustom {
    Page<Transaction> findAllBySenderAccount_User_IdOrRecipientAccount_User_Id(
            Integer senderUserId, Integer recipientUserId, Pageable pageable);

    List<Transaction> findByTransactionType_NameAndSenderAccount_User_IdAndCreatedAtBetween(
            String transactionTypeName, Integer userId, LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByTransactionType_NameAndRecipientAccount_User_IdAndCreatedAtBetween(
            String transactionTypeName, Integer userId, LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findTop5BySenderAccountAndTransactionType_NameOrderByCreatedAtDesc(
            Account senderAccount, String transactionTypeName);

//    @Query("SELECT t FROM Transaction t " +
//            "JOIN t.senderAccount sa " +
//            "JOIN sa.user su " +
//            "LEFT JOIN t.recipientAccount ra " +
//            "LEFT JOIN ra.user ru " +
//            "LEFT JOIN t.transferCategory tc " +
//            "LEFT JOIN t.topupMethod tm " +
//            "JOIN t.transactionType tt " +
//            "WHERE sa.user.id = :userId " +
//            "AND (t.isDeleted = false) " +
//            "AND (:keyword IS NULL OR (" +
//            "LOWER(su.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "(ra IS NOT NULL AND LOWER(ru.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
//            "LOWER(t.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
//            ")) " +
//            "AND (:transactionType IS NULL OR tt.name = :transactionType) " +
//            "AND (:transferCategory IS NULL OR (tc IS NOT NULL AND tc.name = :transferCategory)) " +
//            "AND (:topupMethod IS NULL OR (tm IS NOT NULL AND tm.name = :topupMethod))")
//    Page<Transaction> searchAndFilterTransactions(
//            @Param("userId") Integer userId,
//            @Param("keyword") String keyword,
//            @Param("transactionType") String transactionType,
//            @Param("transferCategory") String transferCategory,
//            @Param("topupMethod") String topupMethod,
//            Pageable pageable);
}
