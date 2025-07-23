package org.example.smartlawgt.query.services.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.query.documents.NotificationDocument;
import org.example.smartlawgt.query.dtos.NotificationDTO;
import org.example.smartlawgt.query.repositories.NotificationMongoRepository;
import org.example.smartlawgt.query.services.define.INotificationQueryService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationQueryService implements INotificationQueryService {
    private final NotificationMongoRepository notificationMongoRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public NotificationDTO getNotificationById(UUID notificationId) {
        NotificationDocument document = notificationMongoRepository.findById(notificationId.toString())
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        return mapToDTO(document);
    }

    @Override
    public List<NotificationDTO> getNotificationsByUser(UUID userId) {
        return notificationMongoRepository.findByUserId(userId.toString())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    @Override
    public long getUnreadCount(UUID userId) {
        return notificationMongoRepository.countByUserIdAndRead(userId.toString(), false);
    }

    @Override
    public List<NotificationDTO> getUnreadNotifications(UUID userId) {
        return notificationMongoRepository.findByUserIdAndRead(userId.toString(), false)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    private NotificationDTO mapToDTO(NotificationDocument document) {
        return NotificationDTO.builder()
                .notificationId(document.getNotificationId())
                .userId(document.getUserId())
                .title(document.getTitle())
                .content(document.getContent())
                .created(document.getCreated())
                .isRead(document.isRead())
                .isEnable(document.isEnable())
                .build();
    }
}