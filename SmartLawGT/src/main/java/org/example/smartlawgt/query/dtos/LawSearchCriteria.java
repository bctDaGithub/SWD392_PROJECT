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
public class LawSearchCriteria {
    private String lawNumber;
    private UUID lawTypeId;
    private String status;
    private String issuingBody;
    private LocalDateTime effectiveDateFrom;
    private LocalDateTime effectiveDateTo;
    private LocalDateTime expiryDateFrom;
    private LocalDateTime expiryDateTo;
    private String searchText;
}
