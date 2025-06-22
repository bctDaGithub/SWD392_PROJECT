package org.example.smartlawgt.command.services.implement;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.entities.UsagePackageEntity;
import org.example.smartlawgt.command.repositories.UsagePackageRepository;
import org.example.smartlawgt.command.services.define.IUsagePackageCommandService;
import org.example.smartlawgt.events.usage_package.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.example.smartlawgt.config.RabbitMQConfig.*;

@Service
@RequiredArgsConstructor
public class UsagePackageCommandService implements IUsagePackageCommandService {

    private final UsagePackageRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public UsagePackageEntity createPackage(UsagePackageEntity pkg) {
        pkg.setIsEnable(true);
        pkg.setUsagePackageId(UUID.randomUUID());
        UsagePackageEntity saved = repository.save(pkg);

        UsagePackageCreatedEvent event = new UsagePackageCreatedEvent();
        event.setUsagePackageId(saved.getUsagePackageId());
        event.setName(saved.getName());
        event.setDescription(saved.getDescription());
        event.setPrice(saved.getPrice());
        event.setDailyLimit(saved.getDailyLimit());
        event.setDaysLimit(saved.getDaysLimit());
        event.setEnable(saved.getIsEnable());

        rabbitTemplate.convertAndSend(USAGE_PACKAGE_EXCHANGE, USAGE_PACKAGE_ROUTING_KEY_CREATED, event);
        return saved;
    }

    @Override
    public UsagePackageEntity updatePackage(UUID id, UsagePackageEntity pkg) {
        UsagePackageEntity existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));

        if (pkg.getName() != null) existing.setName(pkg.getName());
        if (pkg.getDescription() != null) existing.setDescription(pkg.getDescription());
        if (pkg.getPrice() != null) existing.setPrice(pkg.getPrice());
        if (pkg.getDailyLimit() != null) existing.setDailyLimit(pkg.getDailyLimit());
        if (pkg.getDaysLimit() != null) existing.setDaysLimit(pkg.getDaysLimit());
        existing.setUpdateDate(LocalDateTime.now());

        UsagePackageEntity saved = repository.save(existing);

        UsagePackageUpdatedEvent event = new UsagePackageUpdatedEvent();
        event.setUsagePackageId(saved.getUsagePackageId());
        event.setName(saved.getName());
        event.setDescription(saved.getDescription());
        event.setPrice(saved.getPrice());
        event.setDailyLimit(saved.getDailyLimit());
        event.setDaysLimit(saved.getDaysLimit());
        event.setEnable(saved.getIsEnable());

        rabbitTemplate.convertAndSend(USAGE_PACKAGE_EXCHANGE, USAGE_PACKAGE_ROUTING_KEY_UPDATED, event);
        return saved;
    }

    @Override
    public void disablePackage(UUID id) {
        UsagePackageEntity pkg = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));
        pkg.setIsEnable(false);
        pkg.setUpdateDate(LocalDateTime.now());
        repository.save(pkg);

        UsagePackageStatusEvent event = new UsagePackageStatusEvent();
        event.setUsagePackageId(id);
        event.setEnable(false);

        rabbitTemplate.convertAndSend(USAGE_PACKAGE_EXCHANGE, USAGE_PACKAGE_ROUTING_KEY_DISABLED, event);
    }

    @Override
    public void enablePackage(UUID id) {
        UsagePackageEntity pkg = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));
        pkg.setIsEnable(true);
        pkg.setUpdateDate(LocalDateTime.now());
        repository.save(pkg);

        UsagePackageStatusEvent event = new UsagePackageStatusEvent();
        event.setUsagePackageId(id);
        event.setEnable(true);

        rabbitTemplate.convertAndSend(USAGE_PACKAGE_EXCHANGE, USAGE_PACKAGE_ROUTING_KEY_ENABLED, event);
    }
}