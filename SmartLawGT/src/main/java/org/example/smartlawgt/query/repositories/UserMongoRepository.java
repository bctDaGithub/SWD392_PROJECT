package org.example.smartlawgt.query.repositories;

import org.example.smartlawgt.query.documents.UsagePackageDocument;
import org.example.smartlawgt.query.documents.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface UserMongoRepository extends MongoRepository<UserDocument, UUID> {
    UserDocument findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
    UserDocument findByRole(String role);
    UserDocument findByUserId(UUID userId);
}
