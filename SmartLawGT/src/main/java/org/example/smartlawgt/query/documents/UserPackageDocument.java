package org.example.smartlawgt.query.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.smartlawgt.command.entities.TransactionMethod;
import org.example.smartlawgt.command.entities.UserPackageStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "user_packages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPackageDocument {
    @Id
    private String _id;

    private Long userPackageId;           // SQL ID
    private UUID userId;
    private String userName;

    private UUID usagePackageId;
    private String packageName;
    private Float packagePrice;
    private Integer dailyLimit;
    private Integer daysLimit;

    private LocalDateTime transactionDate;
    private TransactionMethod transactionMethod;
    private LocalDateTime expirationDate;

    private UserPackageStatus status;     // Enum: ACTIVE, EXPIRED, BLOCKED

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
