package id.co.bankbsi.rizqtracker.repository;

import id.co.bankbsi.rizqtracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    public List<Transaction> findAllBySenderAccount_User_Id(Integer userId);
}
