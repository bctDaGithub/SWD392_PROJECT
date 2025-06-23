package org.example.smartlawgt.command.services.implement;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.entities.UsagePackageEntity;
import org.example.smartlawgt.command.entities.UserPackageEntity;
import org.example.smartlawgt.command.entities.UserPackageStatus;
import org.example.smartlawgt.command.repositories.UsagePackageRepository;
import org.example.smartlawgt.command.repositories.UserPackageRepository;
import org.example.smartlawgt.command.services.define.IUserPackageCommandService;
import org.example.smartlawgt.config.RabbitMQConfig;
import org.example.smartlawgt.events.user_package.UserPackageCreatedEvent;
import org.example.smartlawgt.events.user_package.UserPackageStatusEvent;
import org.example.smartlawgt.events.user_package.UserPackageUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserPackageCommandService implements IUserPackageCommandService {
    private final UserPackageRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final UsagePackageRepository usagePackageRepository;

    @Override
    public UserPackageEntity recordPurchase(UserPackageEntity purchase) {
        LocalDateTime now = LocalDateTime.now();

        // ⚠️ Truy xuất usagePackage đầy đủ từ DB
        UsagePackageEntity usagePackage = usagePackageRepository.findById(purchase.getUsagePackage().getUsagePackageId())
                .orElseThrow(() -> new RuntimeException("Usage package not found"));

        purchase.setUsagePackage(usagePackage); // Gán lại đầy đủ
        purchase.setTransactionDate(now);

        // ✅ Tính expiration date đúng theo daysLimit thực tế
        purchase.setExpirationDate(now.plusDays(usagePackage.getDaysLimit()));

        purchase.setStatus(UserPackageStatus.ACTIVE);
        purchase.setCreatedDate(now);
        purchase.setUpdatedDate(now);

        UserPackageEntity savedEntity = repository.save(purchase);

        // Tạo event
        UserPackageCreatedEvent event = new UserPackageCreatedEvent();
        event.setId(savedEntity.getId());
        event.setUserId(savedEntity.getUser().getUserId());
        event.setUsagePackageId(savedEntity.getUsagePackage().getUsagePackageId());
        event.setPackageName(usagePackage.getName());
        event.setPackagePrice(usagePackage.getPrice());
        event.setDailyLimit(usagePackage.getDailyLimit());
        event.setDaysLimit(usagePackage.getDaysLimit());
        event.setTransactionDate(now);
        event.setExpirationDate(purchase.getExpirationDate());
        event.setTransactionMethod(purchase.getTransactionMethod());
        event.setStatus(purchase.getStatus());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_PACKAGE_EXCHANGE,
                RabbitMQConfig.USER_PACKAGE_ROUTING_KEY_CREATED,
                event
        );

        return savedEntity;
    }



    @Override
    public void updateUserPackage(UserPackageEntity updatedPackage) {
        repository.findById(updatedPackage.getId())
                .ifPresent(entity -> {
                    UserPackageEntity savedEntity = repository.save(updatedPackage);

                    UserPackageUpdatedEvent event = new UserPackageUpdatedEvent();
                    event.setId(savedEntity.getId());
                    event.setUserId(savedEntity.getUser().getUserId());
                    event.setUsagePackageId(savedEntity.getUsagePackage().getUsagePackageId());
                    event.setPackageName(savedEntity.getUsagePackage().getName());
                    event.setTransactionDate(savedEntity.getTransactionDate());
                    event.setExpirationDate(savedEntity.getExpirationDate());
                    event.setStatus(savedEntity.getStatus());

                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.USER_PACKAGE_EXCHANGE,
                            RabbitMQConfig.USER_PACKAGE_ROUTING_KEY_UPDATED,
                            event
                    );
                });
    }

    @Override
    public void expireUserPackage(Long id) {
        repository.findById(id)
                .ifPresent(entity -> {
                    entity.setStatus(UserPackageStatus.EXPIRED);
                    repository.save(entity);

                    UserPackageStatusEvent event = new UserPackageStatusEvent();
                    event.setId(id);
                    event.setStatus(UserPackageStatus.EXPIRED);

                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.USER_PACKAGE_EXCHANGE,
                            RabbitMQConfig.USER_PACKAGE_ROUTING_KEY_EXPIRED,
                            event
                    );
                });
    }

    @Override
    public void blockUserPackage(Long id) {
        repository.findById(id).ifPresent(entity -> {
            entity.setStatus(UserPackageStatus.BLOCKED);
            repository.save(entity);

            UserPackageStatusEvent event = new UserPackageStatusEvent();
            event.setId(id);
            event.setStatus(UserPackageStatus.BLOCKED);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.USER_PACKAGE_EXCHANGE,
                    RabbitMQConfig.USER_PACKAGE_ROUTING_KEY_STATUS,
                    event
            );
        });
    }

    @Override
    public void unblockUserPackage(Long id) {
        repository.findById(id).ifPresent(entity -> {
            entity.setStatus(UserPackageStatus.ACTIVE);
            repository.save(entity);

            UserPackageStatusEvent event = new UserPackageStatusEvent();
            event.setId(id);
            event.setStatus(UserPackageStatus.ACTIVE);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.USER_PACKAGE_EXCHANGE,
                    RabbitMQConfig.USER_PACKAGE_ROUTING_KEY_STATUS,
                    event
            );
        });
    }

}
