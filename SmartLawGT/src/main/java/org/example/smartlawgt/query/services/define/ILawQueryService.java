package org.example.smartlawgt.query.services.define;

import org.example.smartlawgt.query.dtos.LawDTO;
import org.example.smartlawgt.query.dtos.LawSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ILawQueryService {
    LawDTO getLawById(UUID lawId);

    LawDTO getLawByNumber(String lawNumber);

    Page<LawDTO> getAllLaws(Pageable pageable);

    Page<LawDTO> searchLaws(LawSearchCriteria criteria, Pageable pageable);

    List<LawDTO> getActiveLaws();

    List<LawDTO> getLawsByTypeName(String lawTypeName);

    List<LawDTO> getLawsByIssuingBody(String issuingBody);

}
