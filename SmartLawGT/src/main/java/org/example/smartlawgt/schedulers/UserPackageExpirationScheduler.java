package org.example.smartlawgt.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.command.entities.UserPackageEntity;
import org.example.smartlawgt.command.entities.UserPackageStatus;
import org.example.smartlawgt.command.repositories.UserPackageRepository;
import org.example.smartlawgt.command.services.define.IUserPackageCommandService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPackageExpirationScheduler {

    private final UserPackageRepository repository;
    private final IUserPackageCommandService commandService;

    // Chạy mỗi đêm lúc 1h sáng
    @Scheduled(cron = "0 0 1 * * *")
    public void checkAndExpirePackages() {
        List<UserPackageEntity> expiredPackages = repository.findByExpirationDateBeforeAndStatus(
                LocalDateTime.now(),
                UserPackageStatus.ACTIVE
        );

        for (UserPackageEntity pkg : expiredPackages) {
            commandService.expireUserPackage(pkg.getId());
            log.info("UserPackage {} expired", pkg.getId());
        }
    }
}
