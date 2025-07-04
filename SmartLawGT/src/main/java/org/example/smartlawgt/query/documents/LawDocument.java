package org.example.smartlawgt.query.documents;

import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "laws")
@Builder
public class LawDocument {
    @Id
    private String lawId;

    private String lawNumber;

    private String lawTypeName;

    private String createdByUserId;
    private String createdByUserName;
    private String updateByUserId;
    private String updateByUserName;

    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;
private LocalDateTime issueDate;
    private String status;
    private String issuingBody;
    private String contentUrl;
    private String description;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}


