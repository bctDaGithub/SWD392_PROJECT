package org.example.smartlawgt.command.services.implement;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.entities.UserEntity;
import org.example.smartlawgt.command.repositories.UserRepository;
import org.example.smartlawgt.command.services.define.IUserCommandService;
import org.example.smartlawgt.events.user.UserCreatedEvent;
import org.example.smartlawgt.events.user.UserStatusEvent;
import org.example.smartlawgt.events.user.UserUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCommandService implements IUserCommandService {

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserEntity createUser(UserEntity user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new IllegalStateException("Username already exists");
        }

        user.setRole("USER");
        user.setUserId(UUID.randomUUID());

        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        UserEntity savedUser = userRepository.save(user);

        UserCreatedEvent event = new UserCreatedEvent();
        event.setUserId(savedUser.getUserId());
        event.setEmail(savedUser.getEmail());
        event.setUserName(savedUser.getUserName());
        event.setName(savedUser.getName());
        event.setAvatarUrlText(savedUser.getAvatarUrlText());
        event.setBirthday(savedUser.getBirthday());
        event.setIsActive(savedUser.getIsActive());
        event.setRole(savedUser.getRole());
        event.setCreatedDate(LocalDateTime.now());

        rabbitTemplate.convertAndSend("userCreatedQueue", event);
        return savedUser;
    }

    @Override
    public UserEntity updateUser(UUID userId, UserEntity updatedUser) {
        UserEntity existing = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existing.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalStateException("Email already exists");
            }
            existing.setEmail(updatedUser.getEmail());
        }

        // Check username uniqueness if it's being updated
        if (updatedUser.getUserName() != null && !updatedUser.getUserName().equals(existing.getUserName())) {
            if (userRepository.existsByUserName(updatedUser.getUserName())) {
                throw new IllegalStateException("Username already exists");
            }
            existing.setUserName(updatedUser.getUserName());
        }

        if (updatedUser.getUserName() != null) {
            existing.setUserName(updatedUser.getUserName());
        }
        if (updatedUser.getPassword() != null) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if (updatedUser.getName() != null) {
            existing.setName(updatedUser.getName());
        }
        if (updatedUser.getAvatarUrlText() != null) {
            existing.setAvatarUrlText(updatedUser.getAvatarUrlText());
        }
        if (updatedUser.getBirthday() != null) {
            existing.setBirthday(updatedUser.getBirthday());
        }
        if (updatedUser.getEmail() != null) {
            existing.setEmail(updatedUser.getEmail());
        }
        existing.setUpdateDate(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(existing);

        UserUpdatedEvent event = new UserUpdatedEvent();
        event.setUserId(savedUser.getUserId());
        event.setEmail(savedUser.getEmail());
        event.setUserName(savedUser.getUserName());
        event.setName(savedUser.getName());
        event.setAvatarUrlText(savedUser.getAvatarUrlText());
        event.setBirthday(savedUser.getBirthday());
        event.setIsActive(savedUser.getIsActive());
        event.setRole(savedUser.getRole());
        event.setUpdatedDate(LocalDateTime.now());

        rabbitTemplate.convertAndSend("userUpdatedQueue", event);
        return savedUser;
    }

    @Override
    public void blockUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setIsActive(false);
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        UserStatusEvent event = new UserStatusEvent();
        event.setUserId(userId);
        event.setIsActive(false);
        event.setTimestamp(LocalDateTime.now());

        rabbitTemplate.convertAndSend("userBlockedQueue", event);
    }

    @Override
    public void unblockUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setIsActive(true);
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        UserStatusEvent event = new UserStatusEvent();
        event.setUserId(userId);
        event.setIsActive(true);
        event.setTimestamp(LocalDateTime.now());

        rabbitTemplate.convertAndSend("userUnblockedQueue", event);
    }
}