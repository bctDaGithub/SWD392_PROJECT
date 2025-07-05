package org.example.smartlawgt.query.repositories;

import org.example.smartlawgt.query.documents.LawDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LawMongoRepository extends MongoRepository<LawDocument, String> {
    Optional<LawDocument> findByLawNumber(String lawNumber);

    List<LawDocument> findByStatus(String status);


    List<LawDocument> findByIssuingBody(String issuingBody);

    List<LawDocument> findByLawTypeName(String lawTypeName);
}
