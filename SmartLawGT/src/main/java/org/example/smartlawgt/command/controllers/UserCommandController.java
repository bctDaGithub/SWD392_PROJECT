package org.example.smartlawgt.command.controllers;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.dtos.UserCommandDTO;
import org.example.smartlawgt.command.dtos.UserResponseDTO;
import org.example.smartlawgt.command.entities.UserEntity;
import org.example.smartlawgt.command.mappers.UserMapper;
import org.example.smartlawgt.command.services.define.IUserCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api.command-path}/user")
@RequiredArgsConstructor
public class UserCommandController {

    private final IUserCommandService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCommandDTO dto) {
        UserEntity saved = userService.createUser(UserMapper.toEntity(dto));
        return ResponseEntity.ok(UserMapper.toResponseDTO(saved));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID userId, @RequestBody UserCommandDTO dto) {
        UserEntity updated = userService.updateUser(userId, UserMapper.toEntity(dto));
        return ResponseEntity.ok(UserMapper.toResponseDTO(updated));
    }

    @PutMapping("/block/{userId}")
    public ResponseEntity<Void> blockUser(@PathVariable UUID userId) {
        userService.blockUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/unblock/{userId}")
    public ResponseEntity<Void> unblockUser(@PathVariable UUID userId) {
        userService.unblockUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/change-password")
    public ResponseEntity<UserResponseDTO> changePassword(
            @PathVariable UUID userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        UserEntity updated = userService.changePassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok(UserMapper.toResponseDTO(updated));
    }
}
