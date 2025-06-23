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
                .userPackageId(event.getId())
                .userId(event.getUserId())
                .usagePackageId(event.getUsagePackageId())
                .packageName(event.getPackageName())
                .packagePrice(event.getPackagePrice())
                .dailyLimit(event.getDailyLimit())
                .daysLimit(event.getDaysLimit())
                .transactionDate(event.getTransactionDate())
                .expirationDate(event.getExpirationDate())
                .transactionMethod(event.getTransactionMethod())
                .status(event.getStatus())
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
                    document.setPackagePrice(event.getPackagePrice());
                    document.setDailyLimit(event.getDailyLimit());
                    document.setDaysLimit(event.getDaysLimit());
                    document.setTransactionDate(event.getTransactionDate());
                    document.setExpirationDate(event.getExpirationDate());
                    document.setTransactionMethod(event.getTransactionMethod());
                    document.setStatus(event.getStatus());
                    document.setUpdatedDate(LocalDateTime.now());
                    userPackageRepository.save(document);
                });
    }

    @RabbitListener(queues = "userPackageExpiredQueue")
    public void handleUserPackageExpired(UserPackageStatusEvent event) {
        userPackageRepository.findByUserPackageId(event.getId())
                .ifPresent(document -> {
                    document.setStatus(event.getStatus()); // ví dụ: EXPIRED hoặc BLOCKED
                    document.setUpdatedDate(LocalDateTime.now());
                    userPackageRepository.save(document);
                });
    }

    @RabbitListener(queues = "userPackageStatusQueue")
    public void handleUserPackageStatusChanged(UserPackageStatusEvent event) {
        userPackageRepository.findByUserPackageId(event.getId())
                .ifPresent(document -> {
                    document.setStatus(event.getStatus());
                    document.setUpdatedDate(LocalDateTime.now());
                    userPackageRepository.save(document);
                });
    }

}
