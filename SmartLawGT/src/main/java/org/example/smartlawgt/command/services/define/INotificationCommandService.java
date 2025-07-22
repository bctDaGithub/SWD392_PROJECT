package org.example.smartlawgt.command.services.define;

import org.example.smartlawgt.command.entities.notification.NotificationType;

import java.util.UUID;

public interface INotificationCommandService {
    String sendNotification(String userId, String title, String content);
    void sendBroadcastNotification(String title, String content, NotificationType type);
    void updateNotification(UUID notificationId, String title, String content);
    void deleteNotification(UUID notificationId);
    void notifyLawCreated(String lawNumber);
    void notifyLawUpdated(String lawNumber);
    void markAsRead(UUID notificationId);
    void markAllAsRead(UUID userId);

}
