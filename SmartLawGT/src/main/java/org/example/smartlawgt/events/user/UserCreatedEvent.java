package org.example.smartlawgt.events.user;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserCreatedEvent {
    private UUID userId;
    private String email;
    private String userName;
    private String name;
    private String avatarUrlText;
    private LocalDate birthday;
    private Boolean isActive;
    private String role;
    private LocalDateTime createdDate;
}