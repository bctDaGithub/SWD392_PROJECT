package org.example.smartlawgt.command.dtos.Law;

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
public class UpdateLawCommand {
    private UUID lawTypeId;
    private String lawNumber;
    private LocalDateTime issueDate;
    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;
    private String status;
    private String UpdateByUserId;
    private String issuingBody;
    private String contentUrl;
    private String description;
}
