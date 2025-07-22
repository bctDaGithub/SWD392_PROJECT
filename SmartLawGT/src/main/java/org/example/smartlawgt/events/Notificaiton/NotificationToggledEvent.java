package org.example.smartlawgt.events.Notificaiton;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationToggledEvent {
    private String notificationId;
    private boolean enable;
}
