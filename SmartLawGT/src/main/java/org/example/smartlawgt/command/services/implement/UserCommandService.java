package org.example.smartlawgt.command.services.implement;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.entities.UserEntity;
import org.example.smartlawgt.command.repositories.UserRepository;
import org.example.smartlawgt.command.services.define.IUserCommandService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCommandService implements IUserCommandService {

    private final UserRepository userRepository;

    @Override
    public UserEntity createUser(UserEntity user) {
        user.setRole("USER");
        user.setUserId(UUID.randomUUID());
        return userRepository.save(user);
    }

    @Override
    public UserEntity updateUser(UUID userId, UserEntity updatedUser) {
        UserEntity existing = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        existing.setUserName(updatedUser.getUserName());
        existing.setPassword(updatedUser.getPassword());
        existing.setAvatarUrlText(updatedUser.getAvatarUrlText());
        existing.setBirthday(updatedUser.getBirthday());
        existing.setUpdateDate(LocalDateTime.now());
        return userRepository.save(existing);
    }

    @Override
    public void blockUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setIsActive(false);
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void unblockUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setIsActive(true);
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);
    }
}

