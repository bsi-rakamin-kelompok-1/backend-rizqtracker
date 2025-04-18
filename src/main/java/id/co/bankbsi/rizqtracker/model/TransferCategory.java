package id.co.bankbsi.rizqtracker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transfer_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(nullable = false, unique = true)
    private String name;
}