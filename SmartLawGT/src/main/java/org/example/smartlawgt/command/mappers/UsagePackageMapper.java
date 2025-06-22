package org.example.smartlawgt.command.mappers;

import org.example.smartlawgt.command.dtos.UsagePackageDTO;
import org.example.smartlawgt.command.entities.UsagePackageEntity;

import java.time.LocalDateTime;

public class UsagePackageMapper {

    public static UsagePackageEntity toEntity(UsagePackageDTO dto) {
        UsagePackageEntity entity = new UsagePackageEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setDailyLimit(dto.getDailyLimit());
        entity.setDaysLimit(dto.getDaysLimit());
        entity.setCreatedDate(LocalDateTime.now());
        entity.setUpdateDate(LocalDateTime.now());
        return entity;
    }

        public static void updateEntity(UsagePackageEntity entity, UsagePackageDTO dto) {
            if (dto.getName() != null) {
                entity.setName(dto.getName());
            }
            if (dto.getDescription() != null) {
                entity.setDescription(dto.getDescription());
            }
            if (dto.getPrice() != null) {
                entity.setPrice(dto.getPrice());
            }
            if (dto.getDailyLimit() != null) {
                entity.setDailyLimit(dto.getDailyLimit());
            }
            if (dto.getDaysLimit() != null) {
                entity.setDaysLimit(dto.getDaysLimit());
            }
            entity.setUpdateDate(LocalDateTime.now());
        }

    public static UsagePackageDTO toDTO(UsagePackageEntity entity) {
        UsagePackageDTO dto = new UsagePackageDTO();
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setDailyLimit(entity.getDailyLimit());
        dto.setDaysLimit(entity.getDaysLimit());
        return dto;
    }
}
