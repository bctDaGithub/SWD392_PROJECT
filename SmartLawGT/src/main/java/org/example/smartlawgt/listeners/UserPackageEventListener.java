
package org.example.smartlawgt.listeners;


import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.events.user_package.UserPackageCreatedEvent;
import org.example.smartlawgt.events.user_package.UserPackageStatusEvent;
import org.example.smartlawgt.events.user_package.UserPackageUpdatedEvent;
import org.example.smartlawgt.query.documents.UserPackageDocument;
import org.example.smartlawgt.query.repositories.UserPackageMongoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserPackageEventListener {
    private final UserPackageMongoRepository userPackageRepository;

    @RabbitListener(queues = "userPackageCreatedQueue")
    public void handleUserPackageCreated(UserPackageCreatedEvent event) {
        LocalDateTime now = LocalDateTime.now();
        UserPackageDocument document = UserPackageDocument.builder()
                .userPackageId(event.getId())  // SQL Server ID
                .userId(event.getUserId())
                .usagePackageId(event.getUsagePackageId())
                .packageName(event.getPackageName())
                .transactionDate(event.getTransactionDate())
                .expirationDate(event.getExpirationDate())
                .isActive(event.isActive())
                .createdDate(now)
                .updatedDate(now)
                .build();

        userPackageRepository.save(document);
    }

    @RabbitListener(queues = "userPackageUpdatedQueue")
    public void handleUserPackageUpdated(UserPackageUpdatedEvent event) {
        userPackageRepository.findByUserPackageId(event.getId())
                .ifPresent(document -> {
                    document.setPackageName(event.getPackageName());
                    document.setTransactionDate(event.getTransactionDate());
                    document.setExpirationDate(event.getExpirationDate());
                    document.setIsActive(event.isActive());
                    document.setUpdatedDate(LocalDateTime.now());
                    userPackageRepository.save(document);
                });
    }

    @RabbitListener(queues = "userPackageExpiredQueue")
    public void handleUserPackageExpired(UserPackageStatusEvent event) {
        userPackageRepository.findByUserPackageId(event.getId())
                .ifPresent(document -> {
                    document.setIsActive(false);
                    document.setUpdatedDate(LocalDateTime.now());
                    userPackageRepository.save(document);
                });
    }
}