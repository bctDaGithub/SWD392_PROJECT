package org.example.smartlawgt.command.services.define;

import org.example.smartlawgt.command.dtos.CreateLawCommand;
import org.example.smartlawgt.command.dtos.UpdateLawCommand;
import org.example.smartlawgt.command.entities.LawStatus;

import java.util.UUID;

public interface ILawCommandService {
    UUID createLaw(CreateLawCommand command);
    void updateLaw(UUID lawId, UpdateLawCommand command);
    void deleteLaw(UUID lawId);
    void changeLawStatus(UUID lawId, LawStatus status);
}
