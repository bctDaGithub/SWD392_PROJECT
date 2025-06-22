package org.example.smartlawgt.command.services.define;

import org.example.smartlawgt.command.entities.UsagePackageEntity;

import java.util.UUID;

public interface IUsagePackageCommandService {
    UsagePackageEntity createPackage(UsagePackageEntity pkg);
    UsagePackageEntity updatePackage(UUID id, UsagePackageEntity pkg);
    void disablePackage(UUID id);
    void enablePackage(UUID id);
}
