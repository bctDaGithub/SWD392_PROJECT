package org.example.smartlawgt.events.Notificaiton;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreatedEvent {
    private String notificationId;
    private String userId;
    private String title;
    private String content;
    private LocalDateTime timestamp;
}
