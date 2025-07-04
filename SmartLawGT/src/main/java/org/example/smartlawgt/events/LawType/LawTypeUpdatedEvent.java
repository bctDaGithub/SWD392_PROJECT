package org.example.smartlawgt.events.LawType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LawTypeUpdatedEvent {
    private UUID lawTypeId;
    private String LawTypename;
    private LocalDateTime updatedDate;
}
