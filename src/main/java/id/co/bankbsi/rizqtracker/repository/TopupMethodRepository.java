package id.co.bankbsi.rizqtracker.repository;

import id.co.bankbsi.rizqtracker.model.TopupMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopupMethodRepository extends JpaRepository<TopupMethod, Short> {
    Optional<TopupMethod> findByName(String name);
}
