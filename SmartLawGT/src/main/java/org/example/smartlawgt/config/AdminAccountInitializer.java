package org.example.smartlawgt.command.config;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.entities.UserEntity;
import org.example.smartlawgt.events.user.UserCreatedEvent;
import org.example.smartlawgt.command.repositories.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void run(ApplicationArguments args) {
        String adminEmail = "admin@smartlawgt.com";

       UserEntity existingAdmin = userRepository.findByEmail(adminEmail);
        if (existingAdmin == null) {
            UUID userId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            UserEntity admin = UserEntity.builder()
                    .userId(userId)
                    .email(adminEmail)
                    .userName("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .name("Administrator")
                    .role("ADMIN")
                    .isActive(true)
                    .createdDate(now)
                    .build();

            userRepository.save(admin);

            UserCreatedEvent event = new UserCreatedEvent();
            event.setUserId(userId);
            event.setEmail(adminEmail);
            event.setUserName("admin");
            event.setName("Administrator");
            event.setAvatarUrlText(null);
            event.setBirthday(null);
            event.setIsActive(true);
            event.setRole("ADMIN");
            event.setCreatedDate(now);

            rabbitTemplate.convertAndSend("userCreatedQueue", event);

            System.out.println("Admin account created and event published: " + adminEmail);
        } else {
            System.out.println("Admin account already exists.");
        }
    }
}
