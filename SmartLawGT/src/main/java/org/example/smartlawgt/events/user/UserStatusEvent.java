package org.example.smartlawgt.events.user;

import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
public class UserStatusEvent {
    private UUID userId;
    private Boolean isActive;
    private LocalDateTime timestamp;
}