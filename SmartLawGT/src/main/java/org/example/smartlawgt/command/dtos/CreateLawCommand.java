package org.example.smartlawgt.command.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateLawCommand {
    @NotNull(message = "Law type is required")
    private UUID lawTypeId;

    @NotNull(message = "Created by user is required")
    private UUID createdByUserId;

    @NotBlank(message = "Law number is required")
    private String lawNumber;

    private LocalDateTime issueDate;

    @NotNull(message = "Effective date is required")
    private LocalDateTime effectiveDate;

    private LocalDateTime expiryDate;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "Issuing body is required")
    private String issuingBody;

    private String contentUrl;

    private String description;
}
