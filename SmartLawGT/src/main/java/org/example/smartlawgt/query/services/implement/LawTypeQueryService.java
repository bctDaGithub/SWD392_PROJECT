package org.example.smartlawgt.query.services.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.query.documents.LawTypeDocument;
import org.example.smartlawgt.query.dtos.LawTypeQueryDTO;
import org.example.smartlawgt.query.repositories.LawTypeMongoRepository;
import org.example.smartlawgt.query.services.define.ILawTypeQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class LawTypeQueryService implements ILawTypeQueryService {
    private final LawTypeMongoRepository lawTypeDocumentRepository;
    @Override
    public LawTypeQueryDTO getLawTypeById(UUID lawTypeId) {
        LawTypeDocument document = lawTypeDocumentRepository.findByLawTypeId(String.valueOf(lawTypeId))
                .orElseThrow(() -> new IllegalArgumentException("Law type not found: " + lawTypeId));

        return mapToDTO(document);
    }
    @Override
    public List<LawTypeQueryDTO> getAllLawTypes() {
        return lawTypeDocumentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<LawTypeQueryDTO> getActiveLawTypes() {
        return lawTypeDocumentRepository.findByIsDeletedFalse().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<LawTypeQueryDTO> searchLawTypes(String name) {
        return lawTypeDocumentRepository.findByLawTypenameContainingIgnoreCaseAndIsDeletedFalse(name)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private LawTypeQueryDTO mapToDTO(LawTypeDocument document) {
        return LawTypeQueryDTO.builder()
                .lawTypeId(UUID.fromString(document.getLawTypeId()))
                .name(document.getLawTypename())
                .isDeleted(document.getIsDeleted())
                .createdDate(document.getCreatedDate())
                .updatedDate(document.getUpdatedDate())
                .build();
    }
}
