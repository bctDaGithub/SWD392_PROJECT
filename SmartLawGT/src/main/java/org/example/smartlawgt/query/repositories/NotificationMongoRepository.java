package org.example.smartlawgt.query.repositories;

import org.example.smartlawgt.query.documents.NotificationDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationMongoRepository extends MongoRepository<NotificationDocument, String> {
    List<NotificationDocument> findByUserId(String userId);
    List<NotificationDocument> findByUserIdAndRead(String userId, boolean isRead);
    Page<NotificationDocument> findByUserId(String userId, Pageable pageable);
    long countByUserIdAndRead(String userId, boolean isRead);
}
