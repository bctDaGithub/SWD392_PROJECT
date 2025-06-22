package org.example.smartlawgt.command.services.implement;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.entities.UsagePackageEntity;
import org.example.smartlawgt.command.repositories.UsagePackageRepository;
import org.example.smartlawgt.command.services.define.IUsagePackageCommandService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsagePackageCommandService implements IUsagePackageCommandService {

    private final UsagePackageRepository repository;

    @Override
    public UsagePackageEntity createPackage(UsagePackageEntity pkg) {
        pkg.setIsEnable(true);
        pkg.setUsagePackageId(UUID.randomUUID());
        return repository.save(pkg);
    }

    @Override
    public UsagePackageEntity updatePackage(UUID id, UsagePackageEntity pkg) {
        UsagePackageEntity existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));

        if (pkg.getName() != null) {
            existing.setName(pkg.getName());
        }
        if (pkg.getDescription() != null) {
            existing.setDescription(pkg.getDescription());
        }
        if (pkg.getPrice() != null) {
            existing.setPrice(pkg.getPrice());
        }
        if (pkg.getDailyLimit() != null) {
            existing.setDailyLimit(pkg.getDailyLimit());
        }
        if (pkg.getDaysLimit() != null) {
            existing.setDaysLimit(pkg.getDaysLimit());
        }
        existing.setUpdateDate(LocalDateTime.now());

        return repository.save(existing);
    }

    @Override
    public void disablePackage(UUID id) {
        UsagePackageEntity pkg = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));
        pkg.setIsEnable(false);
        pkg.setUpdateDate(LocalDateTime.now());
        repository.save(pkg);
    }

    @Override
    public void enablePackage(UUID id) {
        UsagePackageEntity pkg = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));
        pkg.setIsEnable(true);
        pkg.setUpdateDate(LocalDateTime.now());
        repository.save(pkg);
    }
}

