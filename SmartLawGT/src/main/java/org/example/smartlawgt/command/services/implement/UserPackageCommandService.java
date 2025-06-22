package org.example.smartlawgt.command.services.implement;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.entities.UserPackageEntity;
import org.example.smartlawgt.command.repositories.UserPackageRepository;
import org.example.smartlawgt.command.services.define.IUserPackageCommandService;
import org.example.smartlawgt.config.RabbitMQConfig;
import org.example.smartlawgt.events.user_package.UserPackageCreatedEvent;
import org.example.smartlawgt.events.user_package.UserPackageStatusEvent;
import org.example.smartlawgt.events.user_package.UserPackageUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPackageCommandService implements IUserPackageCommandService {
    private final UserPackageRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public UserPackageEntity recordPurchase(UserPackageEntity purchase) {
        UserPackageEntity savedEntity = repository.save(purchase);

        UserPackageCreatedEvent event = new UserPackageCreatedEvent();
        event.setId(savedEntity.getId());
        event.setUserId(savedEntity.getUser().getUserId());
        event.setUsagePackageId(savedEntity.getUsagePackage().getUsagePackageId());
        event.setPackageName(savedEntity.getUsagePackage().getName());
        event.setTransactionDate(savedEntity.getTransactionDate());
        // You may need to add expirationDate to UserPackageEntity if required
        // event.setExpirationDate(savedEntity.getExpirationDate());
        event.setActive(Boolean.TRUE.equals(savedEntity.getUser().getIsActive()));

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
                    // event.setExpirationDate(savedEntity.getExpirationDate());
                    event.setActive(Boolean.TRUE.equals(savedEntity.getUser().getIsActive()));

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
                    entity.getUser().setIsActive(false);
                    repository.save(entity);

                    UserPackageStatusEvent event = new UserPackageStatusEvent();
                    event.setId(id);

                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.USER_PACKAGE_EXCHANGE,
                            RabbitMQConfig.USER_PACKAGE_ROUTING_KEY_EXPIRED,
                            event
                    );
                });
    }
}