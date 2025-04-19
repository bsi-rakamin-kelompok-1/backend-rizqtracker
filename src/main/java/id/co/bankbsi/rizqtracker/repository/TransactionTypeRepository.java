package id.co.bankbsi.rizqtracker.repository;

import id.co.bankbsi.rizqtracker.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType, Short> {
    public Optional<TransactionType> findByName(String name) throws RuntimeException;
}
