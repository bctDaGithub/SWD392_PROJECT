package org.example.smartlawgt.events.Law;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LawDeletedEvent {
    private UUID lawId;
    private String lawNumber;

}
