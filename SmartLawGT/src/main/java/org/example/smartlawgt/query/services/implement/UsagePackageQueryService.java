package org.example.smartlawgt.query.services.implement;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.query.documents.UsagePackageDocument;
import org.example.smartlawgt.query.repositories.UsagePackageMongoRepository;
import org.example.smartlawgt.query.services.define.IUsagePackageQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsagePackageQueryService implements IUsagePackageQueryService {
    private final UsagePackageMongoRepository usagePackageMongoRepository;

    @Override
    public List<UsagePackageDocument> findAllPackages() {
        return usagePackageMongoRepository.findAllOrdered();
    }

    @Override
    public List<UsagePackageDocument> findAllActivePackages() {
        return usagePackageMongoRepository.findByIsEnableTrue();
    }

    @Override
    public Optional<UsagePackageDocument> findActivePackageById(UUID packageId) {
        return usagePackageMongoRepository.findByUsagePackageIdAndIsEnableTrue(packageId);
    }

    @Override
    public List<UsagePackageDocument> searchActivePackagesByName(String name) {
        return usagePackageMongoRepository.findByNameContainingIgnoreCaseAndIsEnableTrue(name);
    }

    @Override
    public List<UsagePackageDocument> findPackagesWithinPrice(Float maxPrice) {
        return usagePackageMongoRepository.findByPriceLessThanEqualAndIsEnableTrue(maxPrice);
    }
}