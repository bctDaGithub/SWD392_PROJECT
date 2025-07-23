package org.example.smartlawgt.events.user_package;

import lombok.Data;
import org.example.smartlawgt.command.entities.TransactionMethod;
import org.example.smartlawgt.command.entities.UserPackageStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserPackageCreatedEvent {
    private Long id;
    private UUID userId;
    private String userName;  // Add userName field
    private UUID usagePackageId;
    private String packageName;
    private Float packagePrice;
    private Integer dailyLimit;
    private Integer daysLimit;
    private LocalDateTime transactionDate;
    private LocalDateTime expirationDate;
    private TransactionMethod transactionMethod;
    private UserPackageStatus status;
}
