package id.co.bankbsi.rizqtracker.model;

import lombok.*;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @Column(name = "account_number", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @SequenceGenerator(
            name = "account_seq",
            sequenceName = "account_sequence",
            initialValue = 700000000,
            allocationSize = 1
    )
    private Long accountNumber;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private Long balance = 0L;
}