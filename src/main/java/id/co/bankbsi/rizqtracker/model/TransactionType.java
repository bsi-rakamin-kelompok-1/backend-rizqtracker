package id.co.bankbsi.rizqtracker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transaction_types")
@Data
public class TransactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(nullable = false, unique = true)
    private String name;

}
