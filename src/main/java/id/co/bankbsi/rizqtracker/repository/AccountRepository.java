package id.co.bankbsi.rizqtracker.repository;

import id.co.bankbsi.rizqtracker.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(Long accountNumber);
    Optional<Account> findByUserId(Integer id);
}
