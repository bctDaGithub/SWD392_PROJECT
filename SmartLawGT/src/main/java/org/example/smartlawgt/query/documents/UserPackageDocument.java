package org.example.smartlawgt.query.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.smartlawgt.command.entities.TransactionMethod;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "user_packages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPackageDocument {
    @Id
    private String _id;  // MongoDB's internal ID
    private Long userPackageId;  // Matching SQL Server ID (using Long since UserPackageEntity uses Long)
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
    private Boolean isActive;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}