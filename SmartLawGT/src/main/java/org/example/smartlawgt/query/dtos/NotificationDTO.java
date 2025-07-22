package org.example.smartlawgt.query.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private UUID notificationId;
    private UUID userId;
    private String title;
    private String content;
    private LocalDateTime created;
    private boolean isRead;
    private boolean isEnable;
}
