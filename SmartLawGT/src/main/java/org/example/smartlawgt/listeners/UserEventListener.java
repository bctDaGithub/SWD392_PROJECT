package org.example.smartlawgt.listeners;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.events.user.*;
import org.example.smartlawgt.query.documents.UserDocument;
import org.example.smartlawgt.query.repositories.UserMongoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final UserMongoRepository userMongoRepository;

    @RabbitListener(queues = "userCreatedQueue")
    public void handleUserCreated(UserCreatedEvent event) {
        LocalDateTime now = LocalDateTime.now();
        UserDocument document = UserDocument.builder()
                .userId(event.getUserId())
                .email(event.getEmail())
                .userName(event.getUserName())
                .name(event.getName())
                .avatarUrlText(event.getAvatarUrlText())
                .birthday(event.getBirthday())
                .isActive(event.getIsActive())
                .role(event.getRole())
                .createdAt(now)
                .updatedAt(now)
                .build();

        userMongoRepository.save(document);
    }

    @RabbitListener(queues = "userUpdatedQueue")
    public void handleUserUpdated(UserUpdatedEvent event) {
        UserDocument existingUser = userMongoRepository.findByUserId(event.getUserId());
        if (existingUser != null) {
            existingUser.setEmail(event.getEmail());
            existingUser.setUserName(event.getUserName());
            existingUser.setName(event.getName());
            existingUser.setAvatarUrlText(event.getAvatarUrlText());
            existingUser.setBirthday(event.getBirthday());
            existingUser.setActive(event.getIsActive());
            existingUser.setRole(event.getRole());
            existingUser.setUpdatedAt(LocalDateTime.now());
            userMongoRepository.save(existingUser);
        }
    }

    @RabbitListener(queues = "userBlockedQueue")
    public void handleUserBlocked(UserStatusEvent event) {
        UserDocument existingUser = userMongoRepository.findByUserId(event.getUserId());
        if (existingUser != null) {
            existingUser.setActive(false);
            existingUser.setUpdatedAt(event.getTimestamp());
            userMongoRepository.save(existingUser);
        }
    }

    @RabbitListener(queues = "userUnblockedQueue")
    public void handleUserUnblocked(UserStatusEvent event) {
        UserDocument existingUser = userMongoRepository.findByUserId(event.getUserId());
        if (existingUser != null) {
            existingUser.setActive(true);
            existingUser.setUpdatedAt(event.getTimestamp());
            userMongoRepository.save(existingUser);
        }
    }
}