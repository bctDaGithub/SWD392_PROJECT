package org.example.smartlawgt.events.user_package;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserPackageCreatedEvent {
    private Long id;
    private UUID userId;
    private UUID usagePackageId;
    private String packageName;
    private LocalDateTime transactionDate;
    private LocalDateTime expirationDate;
    private boolean isActive;
}