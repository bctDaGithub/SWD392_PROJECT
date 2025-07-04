package org.example.smartlawgt.command.repositories;

import org.example.smartlawgt.command.entities.LawTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LawTypeRepository extends JpaRepository<LawTypeEntity, UUID> {
    boolean existsByName(String name);

    List<LawTypeEntity> findByIsDeletedFalse();
}
