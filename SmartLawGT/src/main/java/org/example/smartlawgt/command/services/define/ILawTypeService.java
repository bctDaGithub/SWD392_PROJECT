package org.example.smartlawgt.command.services.define;

import org.example.smartlawgt.command.dtos.Law.CreateLawTypeCommand;
import org.example.smartlawgt.command.dtos.Law.LawTypeDTO;
import org.example.smartlawgt.command.dtos.Law.UpdateLawTypeCommand;

import java.util.List;
import java.util.UUID;

public interface ILawTypeService {
    UUID createLawType(CreateLawTypeCommand command);
    void updateLawType(UUID lawTypeId, UpdateLawTypeCommand command, UUID userId);
    void deleteLawType(UUID lawTypeId);

}
