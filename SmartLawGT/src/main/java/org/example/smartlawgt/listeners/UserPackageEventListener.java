package org.example.smartlawgt.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.command.repositories.UserPackageRepository;
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
@Slf4j
public class UserPackageEventListener {

    private final UserPackageMongoRepository userPackageRepository;
    private final UserPackageRepository sqlUserPackageRepository; // Rename SQL repository to avoid naming conflict

    @RabbitListener(queues = "userPackageCreatedQueue")
    public void handleUserPackageCreated(UserPackageCreatedEvent event) {
        LocalDateTime now = LocalDateTime.now();

        // Fallback mechanism if userName is null in event
        String userName = event.getUserName();
        if (userName == null || userName.isEmpty()) {
            log.warn("userName is null in event for userPackageId: {}, attempting fallback", event.getId());
            // Fallback: query SQL database to get userName
            userName = getUserNameFromSql(event.getId());
        }

        UserPackageDocument document = UserPackageDocument.builder()
                .userPackageId(event.getId())
                .userId(event.getUserId())
                .userName(userName)  // Use userName with fallback
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

    private String getUserNameFromSql(Long userPackageId) {
        try {
            return sqlUserPackageRepository.findById(userPackageId)
                    .map(entity -> entity.getUser().getUserName())
                    .orElse("Unknown User");
        } catch (Exception e) {
            log.error("Failed to get userName from SQL for userPackageId: {}", userPackageId, e);
            return "Unknown User";
        }
    }

    @RabbitListener(queues = "userPackageUpdatedQueue")
    public void handleUserPackageUpdated(UserPackageUpdatedEvent event) {
        userPackageRepository.findByUserPackageId(event.getId())
                .ifPresent(document -> {
                    document.setUserName(event.getUserName());  // Add userName update
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
