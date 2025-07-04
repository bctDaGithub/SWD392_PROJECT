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
public class LawTypeDTO {
    private UUID lawTypeId;
    private String name;
    private Boolean isDeleted;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
