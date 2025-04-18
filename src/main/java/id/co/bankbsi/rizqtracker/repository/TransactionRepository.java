package id.co.bankbsi.rizqtracker.repository;

import id.co.bankbsi.rizqtracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
