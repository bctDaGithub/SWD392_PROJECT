package org.example.smartlawgt.events.Notificaiton;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationUpdatedEvent {
    private String notificationId;
    private String userId;
    private String title;
    private String content;
}
