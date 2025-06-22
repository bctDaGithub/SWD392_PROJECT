package org.example.smartlawgt.command.repositories;

import org.example.smartlawgt.command.entities.UserPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPackageRepository extends JpaRepository<UserPackageEntity, Long> {
}
