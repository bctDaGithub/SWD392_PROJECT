package org.example.smartlawgt.query.services.define;

import org.example.smartlawgt.query.documents.UsagePackageDocument;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUsagePackageQueryService {
    List<UsagePackageDocument> findAllPackages(); // New method for admin
    List<UsagePackageDocument> findAllActivePackages();
    Optional<UsagePackageDocument> findActivePackageById(UUID packageId);
    List<UsagePackageDocument> searchActivePackagesByName(String name);
    List<UsagePackageDocument> findPackagesWithinPrice(Float maxPrice);
}