package org.example.smartlawgt.command.repositories;

import org.example.smartlawgt.command.entities.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
    Page<NotificationEntity> findByUserUserId(UUID userId, Pageable pageable);
    List<NotificationEntity> findByUserUserIdAndIsRead(UUID userId, boolean isRead);
    long countByUserUserIdAndIsRead(UUID userId, boolean isRead);
}
