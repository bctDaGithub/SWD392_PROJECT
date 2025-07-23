package org.example.smartlawgt.events.notificaiton;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDeletedEvent {
    private String notificationId;
    private String userId;
}
