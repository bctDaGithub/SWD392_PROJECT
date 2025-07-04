package org.example.smartlawgt.query.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LawDTO {
    private String lawId;
    private String lawNumber;
    private String lawTypeName;
    private UUID lawTypeId;
    private String createdByUserName;
    private String createdByUserId;
    private String updateByUserId;
    private String updateByUserName;
    private LocalDateTime issueDate;
    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;
    private String status;
    private String issuingBody;
    private String contentUrl;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
