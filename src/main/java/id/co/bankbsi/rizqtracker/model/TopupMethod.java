package id.co.bankbsi.rizqtracker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "topup_methods")
@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class TopupMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(nullable = false, unique = true)
    private String name;
}