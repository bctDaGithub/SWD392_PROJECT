package org.example.smartlawgt.integration.Websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationController {
    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/notification/subscribe")
    @SendToUser("/queue/notifications")
    public String subscribeToNotifications(@Payload String userId) {
        log.info("User {} subscribed to notifications", userId);
        return "Subscribed successfully";
    }

    @MessageMapping("/notification/broadcast")
    @SendTo("/topic/notifications")
    public BroadcastMessage broadcastNotification(@Payload BroadcastMessage message) {
        log.info("Broadcasting notification: {}", message.getContent());
        return message;
    }

    // Send notification to specific user
    public void sendNotificationToUser(String userId, Object notification) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);
    }

    @lombok.Data
    static class BroadcastMessage {
        private String title;
        private String content;
        private String type;
    }
}