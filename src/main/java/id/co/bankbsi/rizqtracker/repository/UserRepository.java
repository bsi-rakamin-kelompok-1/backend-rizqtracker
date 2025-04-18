package id.co.bankbsi.rizqtracker.repository;

import id.co.bankbsi.rizqtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
