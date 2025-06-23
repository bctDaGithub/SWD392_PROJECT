package org.example.smartlawgt.query.services.define;

import org.example.smartlawgt.query.documents.UserPackageDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface IUserPackageQueryService {
    List<UserPackageDocument> findUserActiveSubscriptions(UUID userId);
    Page<UserPackageDocument> getUserSubscriptionHistory(UUID userId, Pageable pageable);
    List<UserPackageDocument> findExpiredSubscriptions();
    List<UserPackageDocument> searchSubscriptionsByPackageName(String packageName);
    List<UserPackageDocument> findAllActiveSubscriptions();
}