package org.example.smartlawgt.command.mappers;

import org.example.smartlawgt.command.dtos.UserCommandDTO;
import org.example.smartlawgt.command.dtos.UserResponseDTO;
import org.example.smartlawgt.command.entities.UserEntity;

import java.time.LocalDateTime;

public class UserMapper {

    public static UserEntity toEntity(UserCommandDTO dto) {
        UserEntity entity = new UserEntity();
        entity.setUserName(dto.getUserName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setName(dto.getName());
        entity.setAvatarUrlText(dto.getAvatarUrlText());
        entity.setBirthday(dto.getBirthday());
        entity.setIsActive(true);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setUpdateDate(LocalDateTime.now());
        return entity;
    }

    public static UserResponseDTO toResponseDTO(UserEntity entity) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(entity.getUserId());
        dto.setUserName(entity.getUserName());
        dto.setEmail(entity.getEmail());
        dto.setAvatarUrlText(entity.getAvatarUrlText());
        dto.setBirthday(entity.getBirthday());
        dto.setCreateDate(entity.getCreatedDate());
        dto.setUpdateDate(entity.getUpdateDate());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    public static void updateEntity(UserEntity entity, UserCommandDTO dto) {
        entity.setUserName(dto.getUserName());
        entity.setPassword(dto.getPassword());
        entity.setName(dto.getName());
        entity.setAvatarUrlText(dto.getAvatarUrlText());
        entity.setBirthday(dto.getBirthday());
        entity.setUpdateDate(LocalDateTime.now());
    }
}
