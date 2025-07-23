package org.example.smartlawgt.command.services.implement;

import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.smartlawgt.command.entities.NotificationEntity;
import org.example.smartlawgt.command.entities.UserEntity;
import org.example.smartlawgt.command.repositories.NotificationRepository;
import org.example.smartlawgt.command.repositories.UserRepository;
import org.example.smartlawgt.command.services.define.INotificationCommandService;
import org.example.smartlawgt.events.notificaiton.NotificationCreatedEvent;
import org.example.smartlawgt.events.notificaiton.NotificationDeletedEvent;
import org.example.smartlawgt.events.notificaiton.NotificationReadEvent;
import org.example.smartlawgt.events.notificaiton.NotificationUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.example.smartlawgt.command.entities.notification.NotificationType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCommandService implements INotificationCommandService {
    private final RabbitTemplate rabbitTemplate;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    @Override
    @Transactional

    public String sendNotification(String userId, String title, String content) {
        UserEntity user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Create notification via CQRS
        String notificationId = UUID.randomUUID().toString();

        NotificationEntity notification = NotificationEntity.builder()
                .notificationId(UUID.fromString(notificationId))
                .user(user)
                .title(title)
                .content(content)
                .isEnable(true)
                .isRead(false)
                .timestamp(LocalDateTime.now())
                .build();


        notificationRepository.save(notification);
        log.info("Notification saved: {}", notificationId);
        // Send to WebSocket for real-time update
     /*   messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications",
                convertToDTO(notification)
        );
*/
        // Send to RabbitMQ for async processing
        NotificationCreatedEvent event = NotificationCreatedEvent.builder()
                .notificationId(notificationId)
                .userId(userId)
                .title(title)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                "notification.exchange",
                "notification.created",
                event
        );
        return notificationId;
    }

    //send notificaiton to all user
    @Override
    @Transactional

    public void sendBroadcastNotification(String title, String content, NotificationType type) {
        List<UserEntity> users = userRepository.findAll();
        for (UserEntity user : users) {
            String notificationId = UUID.randomUUID().toString(); //dùng riêng uuid do trạng thái đọc/chưa đọc mỗi người khác nhau

            NotificationEntity notification = NotificationEntity.builder()
                    .notificationId(UUID.fromString(notificationId))
                    .user(user)
                    .title(title)
                    .content(content)
                    .isEnable(true)
                    .isRead(false)
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);

            // Send event for each notification
            NotificationCreatedEvent event = NotificationCreatedEvent.builder()
                    .notificationId(notificationId)
                    .userId(user.getUserId().toString())
                    .title(title)
                    .content(content)
                    .timestamp(LocalDateTime.now())
                    .build();

            rabbitTemplate.convertAndSend(
                    "notification.exchange",
                    "notification.created",
                    event
            );
        }

        log.info("Broadcast notification sent to {} users: {}", users.size(), title);
    }

    // Update notification
    @Override
    @Transactional

    public void updateNotification(UUID notificationId, String title, String content) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setTitle(title);
        notification.setContent(content);
        notificationRepository.save(notification);

        log.info("Notification updated: {}", notificationId);

        // Send update event
        NotificationUpdatedEvent event = NotificationUpdatedEvent.builder()
                .notificationId(notificationId.toString())
                .userId(notification.getUser().getUserId().toString())
                .title(title)
                .content(content)
                .build();

        rabbitTemplate.convertAndSend(
                "notification.exchange",
                "notification.updated",
                event
        );
    }
//===========================================================================================//
// Delete notification
@Override
@Transactional

public void deleteNotification(UUID notificationId) {
    NotificationEntity notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));

    String userId = notification.getUser().getUserId().toString();
    notificationRepository.delete(notification);

    log.info("Notification deleted: {}", notificationId);

    // Send delete event
    NotificationDeletedEvent event = new NotificationDeletedEvent(
            notificationId.toString(),
            userId
    );

    rabbitTemplate.convertAndSend(
            "notification.exchange",
            "notification.deleted",
            event
    );
}
    @Override

    public void notifyLawCreated(String lawNumber) {
        String title = "Luật mới được ban hành";
        String content = String.format("Luật %s đã được ban hành",
                lawNumber);
        sendBroadcastNotification(title, content, NotificationType.LAW_CREATE);
    }

    // Method to be called from LawService when law is updated
    @Override

    public void notifyLawUpdated(String lawNumber) {
        String title = "Luật được cập nhật";
        String content = String.format("Luật %s đã được cập nhật",
                lawNumber);
        sendBroadcastNotification(title, content, NotificationType.LAW_UPDATE);
    }

//===========================================================================================//

// Mark as read
@Override
@Transactional

public void markAsRead(UUID notificationId) {
    NotificationEntity notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));

    if (!Boolean.TRUE.equals(notification.getIsRead())) {
        notification.setIsRead(true);
        notificationRepository.save(notification);
        log.info("Notification marked as read: {}", notificationId);
    }

    NotificationReadEvent event = new NotificationReadEvent(
            notificationId.toString(),
            notification.getUser().getUserId().toString()
    );

    rabbitTemplate.convertAndSend(
            "notification.exchange",
            "notification.read",
            event
    );
}
//===========================================================================================//

// Mark multiple notifications as read
@Override
@Transactional

public void markAllAsRead(UUID userId) {
    List<NotificationEntity> unreadNotifications = notificationRepository
            .findByUserUserIdAndIsRead(userId, false);

    for (NotificationEntity notification : unreadNotifications) {
        notification.setIsRead(true);

        NotificationReadEvent event = new NotificationReadEvent(
                notification.getNotificationId().toString(),
                userId.toString()
        );

        rabbitTemplate.convertAndSend(
                "notification.exchange",
                "notification.read",
                event
        );
    }

    notificationRepository.saveAll(unreadNotifications);
    log.info("Marked {} notifications as read for user: {}", unreadNotifications.size(), userId);
}



//==========================================================================================//


    // DTO class
    @Data
    @Builder
    public static class NotificationDTO {
        private String notificationId;
        private String userId;
        private String title;
        private String content;
        private boolean isRead;
        private LocalDateTime timestamp;
    }

    // Convert entity to DTO for WebSocket
    private NotificationDTO convertToDTO(NotificationEntity entity) {
        return NotificationDTO.builder()
                .notificationId(entity.getNotificationId().toString())
                .userId(entity.getUser().getUserId().toString())
                .title(entity.getTitle())
                .content(entity.getContent())
                .isRead(entity.getIsRead())
                .timestamp(entity.getTimestamp())
                .build();
    }



}