package org.example.smartlawgt.query.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "usage_packages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsagePackageDocument {
    @Id
    private String _id;  // MongoDB's internal ID
    private UUID usagePackageId;  // Matching SQL Server ID
    private String name;
    private String description;
    private Float price;
    private Integer dailyLimit;
    private Integer daysLimit;
    private Boolean isEnable;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}