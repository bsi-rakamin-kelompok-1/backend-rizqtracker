package id.co.bankbsi.rizqtracker.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_histories")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "sender_account", nullable = false)
    private Account senderAccount;

    @ManyToOne
    @JoinColumn(name = "recipient_account")
    private Account recipientAccount;

    @ManyToOne
    @JoinColumn(name = "transaction_type_id", nullable = false)
    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(name = "transfer_category_id")
    private TransferCategory transferCategory;

    @ManyToOne
    @JoinColumn(name = "topup_method_id")
    private TopupMethod topupMethod;

    @Column(nullable = false)
    private Long amount;

    private String notes;

    @Column(name = "reference_number", unique = true)
    private String referenceNumber;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Sets the deleted timestamp when the transaction is marked as deleted
     */
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        if (Boolean.TRUE.equals(isDeleted)) {
            this.deletedAt = LocalDateTime.now();
        }
    }
}