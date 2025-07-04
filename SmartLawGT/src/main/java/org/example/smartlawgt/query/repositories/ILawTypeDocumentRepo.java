package org.example.smartlawgt.query.repositories;

import org.example.smartlawgt.query.documents.LawTypeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ILawTypeDocumentRepo extends MongoRepository<LawTypeDocument, String> {

    Optional<LawTypeDocument> findByLawTypeId(String lawTypeId);

    void deleteByLawTypeId(String lawTypeId);
    List<LawTypeDocument> findByIsDeletedFalse();
    List<LawTypeDocument> findByLawTypenameContainingIgnoreCaseAndIsDeletedFalse(String name);
}
