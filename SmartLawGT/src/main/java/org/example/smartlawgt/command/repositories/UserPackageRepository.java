package org.example.smartlawgt.command.repositories;

import org.example.smartlawgt.command.entities.UserPackageEntity;
import org.example.smartlawgt.command.entities.UserPackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserPackageRepository extends JpaRepository<UserPackageEntity, Long> {
    List<UserPackageEntity> findByExpirationDateBeforeAndStatus(LocalDateTime now, UserPackageStatus status);

}
