package org.example.smartlawgt.query.repositories;

import org.example.smartlawgt.query.documents.ChatHistoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatHistoryMongoRepository extends MongoRepository<ChatHistoryDocument, String> {
    List<ChatHistoryDocument> findByUserId(UUID userId);
    long countByUserIdAndTimestampBetween(UUID userId, LocalDateTime start, LocalDateTime end);
    List<ChatHistoryDocument> findByUserIdOrderByTimestampDesc(UUID userId);
    long deleteByTimestampBefore(LocalDateTime cutoffDate);
    Optional<ChatHistoryDocument> findTop1ByUserIdOrderByTimestampDesc(UUID userId);

}
