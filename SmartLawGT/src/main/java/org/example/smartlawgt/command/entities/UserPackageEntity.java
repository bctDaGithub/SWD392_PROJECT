package org.example.smartlawgt.command.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.smartlawgt.command.entities.UserPackageStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_package")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "usage_package_id", nullable = false)
    private UsagePackageEntity usagePackage;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_method")
    private TransactionMethod transactionMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserPackageStatus status;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}
