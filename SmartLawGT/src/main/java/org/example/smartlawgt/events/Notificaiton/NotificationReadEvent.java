package org.example.smartlawgt.events.notificaiton;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReadEvent {
    private String notificationId;
    private String userId;
}
