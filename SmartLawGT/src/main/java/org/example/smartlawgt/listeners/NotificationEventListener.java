package org.example.smartlawgt.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.events.notificaiton.NotificationCreatedEvent;
import org.example.smartlawgt.events.notificaiton.NotificationDeletedEvent;
import org.example.smartlawgt.events.notificaiton.NotificationReadEvent;
import org.example.smartlawgt.events.notificaiton.NotificationToggledEvent;
import org.example.smartlawgt.query.documents.NotificationDocument;
import org.example.smartlawgt.query.repositories.NotificationMongoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    private final NotificationMongoRepository notificationMongoRepository;
    private final SimpMessagingTemplate messagingTemplate;
    @RabbitListener(queues = "notification.created.queue")
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        log.info("Received notification created event: {}", event.getNotificationId());

        // Convert Event to Document
        NotificationDocument document = NotificationDocument.builder()
                .notificationId(event.getNotificationId())
                .userId(event.getUserId())
                .title(event.getTitle())
                .content(event.getContent())
                .created(event.getTimestamp())
                .read(false)
                .enable(true)
                .build();

        // Save to MongoDB for queries
        notificationMongoRepository.save(document);
        log.info("Notification saved to MongoDB: {}", document.getNotificationId());

        // Send to WebSocket for real-time update
        messagingTemplate.convertAndSendToUser(
                event.getUserId(),
                "/queue/notifications",
                document
        );
    }

    @RabbitListener(queues = "notification.read.queue")
    public void handleNotificationRead(NotificationReadEvent event) {
        log.info("Received notification read event: {}", event.getNotificationId());

        // Update in MongoDB
        notificationMongoRepository.findNotificationByNotificationId(event.getNotificationId()).ifPresent(doc -> {
            doc.setRead(true);
            notificationMongoRepository.save(doc);

            // Notify via WebSocket
            messagingTemplate.convertAndSendToUser(
                    doc.getUserId(),
                    "/queue/notifications/read",
                    event.getNotificationId()
            );
        });
    }

    @RabbitListener(queues = "notification.toggled.queue")
    public void handleNotificationToggled(NotificationToggledEvent event) {
        log.info("Received notification toggled event: {}", event.getNotificationId());

        // Update in MongoDB
        notificationMongoRepository.findById(event.getNotificationId()).ifPresent(doc -> {
            doc.setEnable(event.isEnable());
            notificationMongoRepository.save(doc);
        });
    }

    @RabbitListener(queues = "notification.deleted.queue")
    public void handleNotificationDeleted(NotificationDeletedEvent event) {
        log.info("Received notification deleted event: {}", event.getNotificationId());

        // Delete from MongoDB
        notificationMongoRepository.findNotificationByNotificationId(event.getNotificationId()).ifPresent(doc -> {
            notificationMongoRepository.delete(doc);
            log.info("Deleted notification from MongoDB: {}", event.getNotificationId());
            messagingTemplate.convertAndSendToUser(
                    event.getUserId(),
                    "/queue/notifications/deleted",
                    event.getNotificationId()
            );
        });
    }
}
