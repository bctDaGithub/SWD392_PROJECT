package org.example.smartlawgt.query.services.define;

import org.example.smartlawgt.query.dtos.LawTypeQueryDTO;

import java.util.List;
import java.util.UUID;

public interface ILawTypeQueryService  {
    LawTypeQueryDTO getLawTypeById(UUID lawTypeId);
    List<LawTypeQueryDTO> getAllLawTypes();
    List<LawTypeQueryDTO> getActiveLawTypes();
    List<LawTypeQueryDTO> searchLawTypes(String name);
}
