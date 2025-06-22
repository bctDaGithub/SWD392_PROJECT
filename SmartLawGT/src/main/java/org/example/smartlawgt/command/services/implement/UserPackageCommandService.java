package org.example.smartlawgt.command.services.implement;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.entities.UserPackageEntity;
import org.example.smartlawgt.command.repositories.UserPackageRepository;
import org.example.smartlawgt.command.services.define.IUserPackageCommandService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPackageCommandService implements IUserPackageCommandService {

    private final UserPackageRepository repository;

    @Override
    public UserPackageEntity recordPurchase(UserPackageEntity purchase) {
        return repository.save(purchase);
    }
}
