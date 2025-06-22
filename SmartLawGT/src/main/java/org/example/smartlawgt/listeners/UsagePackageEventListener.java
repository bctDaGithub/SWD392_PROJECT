package org.example.smartlawgt.listeners;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.events.usage_package.*;
import org.example.smartlawgt.query.documents.UsagePackageDocument;
import org.example.smartlawgt.query.repositories.UsagePackageMongoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UsagePackageEventListener {
    private final UsagePackageMongoRepository usagePackageRepository;

    @RabbitListener(queues = "usagePackageCreatedQueue")
    public void handleUsagePackageCreated(UsagePackageCreatedEvent event) {
        LocalDateTime now = LocalDateTime.now();
        UsagePackageDocument document = UsagePackageDocument.builder()
                .usagePackageId(event.getUsagePackageId()) // This will match SQL Server ID
                .name(event.getName())
                .description(event.getDescription())
                .price(event.getPrice())
                .dailyLimit(event.getDailyLimit())
                .daysLimit(event.getDaysLimit())
                .isEnable(event.isEnable())
                .createdDate(now)
                .updatedDate(now)
                .build();

        usagePackageRepository.save(document);
    }

    @RabbitListener(queues = "usagePackageUpdatedQueue")
    public void handleUsagePackageUpdated(UsagePackageUpdatedEvent event) {
        usagePackageRepository.findByUsagePackageId(event.getUsagePackageId())
                .ifPresent(document -> {
                    document.setName(event.getName());
                    document.setDescription(event.getDescription());
                    document.setPrice(event.getPrice());
                    document.setDailyLimit(event.getDailyLimit());
                    document.setDaysLimit(event.getDaysLimit());
                    document.setIsEnable(event.isEnable());
                    document.setUpdatedDate(LocalDateTime.now());
                    usagePackageRepository.save(document);
                });
    }

    @RabbitListener(queues = "usagePackageDisabledQueue")
    public void handleUsagePackageDisabled(UsagePackageStatusEvent event) {
        usagePackageRepository.findByUsagePackageId(event.getUsagePackageId())
                .ifPresent(document -> {
                    document.setIsEnable(false);
                    document.setUpdatedDate(LocalDateTime.now());
                    usagePackageRepository.save(document);
                });
    }

    @RabbitListener(queues = "usagePackageEnabledQueue")
    public void handleUsagePackageEnabled(UsagePackageStatusEvent event) {
        usagePackageRepository.findByUsagePackageId(event.getUsagePackageId())
                .ifPresent(document -> {
                    document.setIsEnable(true);
                    document.setUpdatedDate(LocalDateTime.now());
                    usagePackageRepository.save(document);
                });
    }
}