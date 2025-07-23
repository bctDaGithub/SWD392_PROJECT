package org.example.smartlawgt.command.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.command.dtos.notification.CreateNotificationRequestDTO;
import org.example.smartlawgt.command.services.define.INotificationCommandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/command/notification")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class NotificationCommandController {
private final INotificationCommandService notificationService;
    // Create notification
    @PostMapping
    public ResponseEntity<String> createNotification(@RequestBody CreateNotificationRequestDTO request) {
        try {
            String notificationId = notificationService.sendNotification(
                    request.getUserId(),
                    request.getTitle(),
                    request.getContent()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(notificationId);
        } catch (Exception ex) {
            log.error("Error creating notification", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + ex.getMessage());
        }
    }

//    // Update notification
//    @PutMapping("/{notificationId}")
//    public ResponseEntity<String> updateNotification(
//            @PathVariable UUID notificationId,
//            @RequestBody UpdateNotificationRequestDTO request) {
//        try {
//            notificationService.updateNotification(notificationId, request.getTitle(), request.getContent());
//            return ResponseEntity.ok("Notification updated successfully");
//        } catch (Exception ex) {
//            log.error("Error updating notification", ex);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error: " + ex.getMessage());
//        }
//    }

    // Delete notification
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable UUID notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok("Notification deleted successfully");
        } catch (Exception ex) {
            log.error("Error deleting notification", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + ex.getMessage());
        }
    }

    // Mark as read
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable UUID notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok("Notification marked as read");
        } catch (Exception ex) {
            log.error("Error marking notification as read", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + ex.getMessage());
        }
    }

    // Mark all as read for user
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<String> markAllAsRead(@PathVariable UUID userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok("All notifications marked as read");
        } catch (Exception ex) {
            log.error("Error marking all notifications as read", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + ex.getMessage());
        }
    }




}
