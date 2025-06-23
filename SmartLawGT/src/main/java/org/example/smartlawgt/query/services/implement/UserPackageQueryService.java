package org.example.smartlawgt.query.services.implement;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.entities.UserPackageStatus;
import org.example.smartlawgt.query.documents.UserPackageDocument;
import org.example.smartlawgt.query.repositories.UserPackageMongoRepository;
import org.example.smartlawgt.query.services.define.IUserPackageQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPackageQueryService implements IUserPackageQueryService {
    private final UserPackageMongoRepository userPackageMongoRepository;

    @Override
    public List<UserPackageDocument> findUserActiveSubscriptions(UUID userId) {
        return userPackageMongoRepository.findActiveSubscriptions(userId, LocalDateTime.now());
    }

    @Override
    public Page<UserPackageDocument> getUserSubscriptionHistory(UUID userId, Pageable pageable) {
        return userPackageMongoRepository.findByUserIdOrderByTransactionDateDesc(userId, pageable);
    }

    @Override
    public List<UserPackageDocument> findExpiredSubscriptions() {
        return userPackageMongoRepository.findByExpirationDateBeforeAndStatus(LocalDateTime.now(), UserPackageStatus.EXPIRED);
    }

    @Override
    public List<UserPackageDocument> searchSubscriptionsByPackageName(String packageName) {
        return userPackageMongoRepository.findByPackageNameContainingIgnoreCase(packageName);
    }

    @Override
    public List<UserPackageDocument> findAllActiveSubscriptions() {
        return userPackageMongoRepository.findAll();
    }
}
