package org.example.smartlawgt.command.mappers;

import org.example.smartlawgt.command.dtos.PurchaseRequestDTO;
import org.example.smartlawgt.command.entities.UserEntity;
import org.example.smartlawgt.command.entities.UsagePackageEntity;
import org.example.smartlawgt.command.entities.UserPackageEntity;

public class UserPackageMapper {
    public static UserPackageEntity toEntity(PurchaseRequestDTO dto) {
        UserEntity user = new UserEntity();
        user.setUserId(dto.getUserId());

        UsagePackageEntity usagePackage = new UsagePackageEntity();
        usagePackage.setUsagePackageId(dto.getUsagePackageId());

        return UserPackageEntity.builder()
                .user(user)
                .usagePackage(usagePackage)
                .transactionMethod(dto.getTransactionMethod())
                .build(); // Tạm thời chưa set transactionDate, expirationDate
    }
}
