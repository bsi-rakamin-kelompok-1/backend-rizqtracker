package id.co.bankbsi.rizqtracker.repository;

import id.co.bankbsi.rizqtracker.model.TransferCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferCategoryRepository extends JpaRepository<TransferCategory, Short> {
}
