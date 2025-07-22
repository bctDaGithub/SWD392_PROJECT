package org.example.smartlawgt.query.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.query.dtos.NotificationDTO;
import org.example.smartlawgt.query.dtos.NotificationSearchCriteria;
import org.example.smartlawgt.query.services.define.INotificationQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/query/notification")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class NotificationQueryController {
    private final INotificationQueryService notificationQueryService;
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable UUID notificationId) {
        try {
            NotificationDTO notification = notificationQueryService.getNotificationById(notificationId);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            log.error("Error getting notification by id", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUser(@PathVariable UUID userId) {
        List<NotificationDTO> notifications = notificationQueryService.getNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@PathVariable UUID userId) {
        List<NotificationDTO> notifications = notificationQueryService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable UUID userId) {
        long count = notificationQueryService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    
}