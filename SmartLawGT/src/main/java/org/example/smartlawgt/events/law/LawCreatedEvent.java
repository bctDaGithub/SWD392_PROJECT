package org.example.smartlawgt.events.law;

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

public class LawCreatedEvent {
    private UUID lawId;
    private String lawNumber;
    private UUID lawTypeId;
    private String lawTypeName;
    private LocalDateTime issueDate;
    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;
    private String CreatedByUserId;
    private String status;
    private String issuingBody;
    private String contentUrl;
    private String description;
/*    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();*/

}
