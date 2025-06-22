package org.example.smartlawgt.query.repositories;

import org.example.smartlawgt.command.entities.UsagePackageEntity;
import org.example.smartlawgt.query.documents.UserPackageDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPackageMongoRepository extends MongoRepository<UserPackageDocument, String> {
    List<UserPackageDocument> findByUserIdAndIsActiveTrue(UUID userId);
    Optional<UserPackageDocument> findByUserPackageId(Long userPackageId);
    Optional<UserPackageDocument> findByUserIdAndUsagePackageIdAndIsActiveTrue(UUID userId, UUID usagePackageId);
    Page<UserPackageDocument> findByUserIdOrderByTransactionDateDesc(UUID userId, Pageable pageable);
    List<UserPackageDocument> findByExpirationDateBeforeAndIsActiveTrue(LocalDateTime date);
    List<UserPackageDocument> findByPackageNameContainingIgnoreCase(String packageName);
    @Query("{'userId': ?0, 'isActive': true, 'expirationDate': {$gt: ?1}}")
    List<UserPackageDocument> findActiveSubscriptions(UUID userId, LocalDateTime now);
}