package org.example.smartlawgt.events.law_type;

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
public class LawTypeCreatedEvent {
    private UUID lawTypeId;
    private String name;
    private LocalDateTime createdDate;
}
