package org.example.smartlawgt.command.services.define;

import org.example.smartlawgt.command.entities.UserPackageEntity;

public interface IUserPackageCommandService {
    UserPackageEntity recordPurchase(UserPackageEntity purchase);
    void expireUserPackage(Long id);
    void updateUserPackage(UserPackageEntity updatedPackage);
    void unblockUserPackage(Long id);
    void blockUserPackage(Long id);
}
