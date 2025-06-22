package org.example.smartlawgt.command.repositories;

import org.example.smartlawgt.command.entities.LawEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LawRepository extends JpaRepository<LawEntity, UUID> {
}
