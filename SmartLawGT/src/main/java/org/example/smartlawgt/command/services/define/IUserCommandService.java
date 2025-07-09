package org.example.smartlawgt.command.services.define;

import org.example.smartlawgt.command.entities.UserEntity;

import java.util.UUID;

public interface IUserCommandService {
    UserEntity createUser(UserEntity user);
    UserEntity updateUser(UUID userId, UserEntity user);
    void blockUser(UUID userId);
    void unblockUser(UUID userId);
    UserEntity changePassword(UUID userId, String oldPassword, String newPassword);
    UserEntity findByEmail(String email);
    UserEntity resetPassword(String email, String newPassword);
}
