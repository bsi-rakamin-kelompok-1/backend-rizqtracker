package id.co.bankbsi.rizqtracker.repository;

import id.co.bankbsi.rizqtracker.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType, Short> {
}
