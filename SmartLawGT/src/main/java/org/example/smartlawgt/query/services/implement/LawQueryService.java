package org.example.smartlawgt.query.services.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.query.documents.LawDocument;
import org.example.smartlawgt.query.dtos.LawDTO;
import org.example.smartlawgt.query.dtos.LawSearchCriteria;
import org.example.smartlawgt.query.repositories.LawMongoRepository;
import org.example.smartlawgt.query.services.define.ILawQueryService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LawQueryService implements ILawQueryService {
    private final LawMongoRepository lawDocumentRepository;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper modelMapper;

    @Override
    public LawDTO getLawById(UUID lawId) {
        log.debug("Getting law by ID: {}", lawId);

        LawDocument lawDocument = lawDocumentRepository.findById(String.valueOf(lawId))
                    .orElseThrow(() -> new IllegalArgumentException("Law not found: " + lawId));

        return mapToDTO(lawDocument);
    }

//    @Override
//    public LawDTO getLawByNumber(String lawNumber) {
//        log.debug("Getting law by number: {}", lawNumber);
//
//        LawDocument lawDocument = lawDocumentRepository.findByLawNumber(lawNumber)
//                .orElseThrow(() -> new IllegalArgumentException("Law not found with number: " + lawNumber));
//
//        return mapToDTO(lawDocument);
//    }

    @Override
    public Page<LawDTO> getAllLaws(Pageable pageable) {
        log.debug("Getting all laws with pagination");

        Page<LawDocument> lawDocuments = lawDocumentRepository.findAll(pageable);
        return lawDocuments.map(this::mapToDTO);
    }

    @Override
    public Page<LawDTO> searchLaws(LawSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching laws with criteria: {}", criteria);

        Query query = buildSearchQuery(criteria);
        query.with(Sort.by(Sort.Direction.DESC, "createdDate"));
        query.with(pageable);

        List<LawDocument> lawDocuments = mongoTemplate.find(query, LawDocument.class);
        long total = mongoTemplate.count(query.skip(0).limit(0), LawDocument.class);

        List<LawDTO> lawDTOs = lawDocuments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(lawDTOs, pageable, () -> total);
    }

    @Override
    public List<LawDTO> getActiveLaws() {
        log.debug("Getting active laws");

        List<LawDocument> activeLaws = lawDocumentRepository.findByStatusOrderByCreatedDateDesc("VALID");
        return activeLaws.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

//    @Override
//    public List<LawDTO> getLawsByTypeName(String lawTypeName) {
//        log.debug("Getting laws by type: {}", lawTypeName);
//
//        List<LawDocument> laws = lawDocumentRepository.findByLawTypeName(lawTypeName);
//        return laws.stream()
//                .map(this::mapToDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<LawDTO> getLawsByIssuingBody(String issuingBody) {
//        log.debug("Getting laws by issuing body: {}", issuingBody);
//
//        List<LawDocument> laws = lawDocumentRepository.findByIssuingBody(issuingBody);
//        return laws.stream()
//                .map(this::mapToDTO)
//                .collect(Collectors.toList());
//    }

    private Query buildSearchQuery(LawSearchCriteria criteria) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (criteria.getLawNumber() != null && !criteria.getLawNumber().isEmpty()) {
            criteriaList.add(Criteria.where("lawNumber").regex(criteria.getLawNumber(), "i"));
        }

        if (criteria.getLawTypeName() != null) {
            criteriaList.add(Criteria.where("lawTypeName").is(criteria.getLawTypeName().toString()));
        }

        if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
            criteriaList.add(Criteria.where("status").is(criteria.getStatus()));
        }

        if (criteria.getIssuingBody() != null && !criteria.getIssuingBody().isEmpty()) {
            criteriaList.add(Criteria.where("issuingBody").regex(criteria.getIssuingBody(), "i"));
        }

        if (criteria.getEffectiveDateFrom() != null && criteria.getEffectiveDateTo() != null) {
            criteriaList.add(Criteria.where("effectiveDate")
                    .gte(criteria.getEffectiveDateFrom())
                    .lte(criteria.getEffectiveDateTo()));
        }

        if (criteria.getExpiryDateFrom() != null && criteria.getExpiryDateTo() != null) {
            criteriaList.add(Criteria.where("expiryDate")
                    .gte(criteria.getExpiryDateFrom())
                    .lte(criteria.getExpiryDateTo()));
        }

        if (criteria.getSearchText() != null && !criteria.getSearchText().isEmpty()) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("lawNumber").regex(criteria.getSearchText(), "i"),
                    Criteria.where("description").regex(criteria.getSearchText(), "i"),
                    Criteria.where("issuingBody").regex(criteria.getSearchText(), "i")
            ));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return query;
    }

    private LawDTO mapToDTO(LawDocument lawDocument) {
        return LawDTO.builder()
                .lawId(lawDocument.getLawId())
                .lawNumber(lawDocument.getLawNumber())
                .lawTypeName(lawDocument.getLawTypeName())
                .createdByUserName(lawDocument.getCreatedByUserName())
                .createdByUserId(lawDocument.getCreatedByUserId())
                .issueDate(lawDocument.getIssueDate())
                .effectiveDate(lawDocument.getEffectiveDate())
                .expiryDate(lawDocument.getExpiryDate())
                .status(lawDocument.getStatus())
                .issuingBody(lawDocument.getIssuingBody())
                .contentUrl(lawDocument.getContentUrl())
                .description(lawDocument.getDescription())
                .createdDate(lawDocument.getCreatedDate())
                .updatedDate(lawDocument.getUpdatedDate())
                .updateByUserId(lawDocument.getUpdateByUserId())
                .updateByUserName(lawDocument.getUpdateByUserName())
                .build();
    }
}

