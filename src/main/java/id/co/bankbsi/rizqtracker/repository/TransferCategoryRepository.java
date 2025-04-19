package id.co.bankbsi.rizqtracker.repository;

import id.co.bankbsi.rizqtracker.model.TransferCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransferCategoryRepository extends JpaRepository<TransferCategory, Short> {
    Optional<TransferCategory> findByName(String name);
}
