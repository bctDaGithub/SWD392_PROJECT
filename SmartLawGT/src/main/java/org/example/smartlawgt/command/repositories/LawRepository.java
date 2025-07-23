package org.example.smartlawgt.command.repositories;

import org.example.smartlawgt.command.entities.LawEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LawRepository extends JpaRepository<LawEntity, String> {
    boolean existsByLawNumber(String lawNumber);

    Optional<LawEntity> findByLawNumber(String lawNumber);

    long countByLawType_LawTypeId(UUID lawTypeId);
    List<LawEntity> findByLawType_LawTypeId(UUID lawTypeId);

    List<LawEntity> findByExpiryDateBefore(LocalDateTime now);
}
