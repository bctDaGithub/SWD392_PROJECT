package org.example.smartlawgt.events.Law;

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

public class LawUpdatedEvent {
    private UUID lawId;
    private String lawNumber;
    private String lawTypeId;
    private String oldLawTypeId;
    private String lawTypeName;
    private LocalDateTime issueDate;
    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;
    private String status;
    private String issuingBody;
    private String contentUrl;
    private String description;
    private String UpdatedByUserId;

}
