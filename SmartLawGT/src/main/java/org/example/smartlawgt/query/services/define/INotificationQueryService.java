package org.example.smartlawgt.query.services.define;

import org.example.smartlawgt.query.dtos.NotificationDTO;
import org.example.smartlawgt.query.dtos.NotificationSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface INotificationQueryService {
    NotificationDTO getNotificationById(UUID notificationId);
    List<NotificationDTO> getNotificationsByUser(UUID userId);
    //Page<NotificationDTO> searchNotifications(NotificationSearchCriteria criteria, Pageable pageable);
    long getUnreadCount(UUID userId);
    List<NotificationDTO> getUnreadNotifications(UUID userId);
}
