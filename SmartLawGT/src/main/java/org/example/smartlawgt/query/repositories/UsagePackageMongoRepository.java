package org.example.smartlawgt.query.repositories;

import org.example.smartlawgt.query.documents.UsagePackageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsagePackageMongoRepository extends MongoRepository<UsagePackageDocument, UUID> {
    @Query(value = "{}", sort = "{ 'createdDate': -1 }")
    List<UsagePackageDocument> findAllOrdered();
    List<UsagePackageDocument> findByIsEnableTrue();
    Optional<UsagePackageDocument> findByUsagePackageIdAndIsEnableTrue(UUID usagePackageId);
    List<UsagePackageDocument> findByNameContainingIgnoreCaseAndIsEnableTrue(String name);
    List<UsagePackageDocument> findByPriceLessThanEqualAndIsEnableTrue(Float maxPrice);
    Optional<UsagePackageDocument> findByUsagePackageId(UUID usagePackageId);
}