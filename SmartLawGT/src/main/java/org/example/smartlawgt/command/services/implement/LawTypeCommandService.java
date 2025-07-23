package org.example.smartlawgt.command.services.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.command.dtos.CreateLawTypeCommand;
import org.example.smartlawgt.command.dtos.UpdateLawCommand;
import org.example.smartlawgt.command.dtos.UpdateLawTypeCommand;
import org.example.smartlawgt.command.entities.LawEntity;
import org.example.smartlawgt.command.entities.LawTypeEntity;
import org.example.smartlawgt.command.repositories.LawRepository;
import org.example.smartlawgt.command.repositories.LawTypeRepository;
import org.example.smartlawgt.command.services.define.ILawTypeService;
import org.example.smartlawgt.config.RabbitMQConfig;
import org.example.smartlawgt.events.law_type.LawTypeCreatedEvent;
import org.example.smartlawgt.events.law_type.LawTypeDeletedEvent;
import org.example.smartlawgt.events.law_type.LawTypeUpdatedCountEvent;
import org.example.smartlawgt.events.law_type.LawTypeUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LawTypeCommandService implements ILawTypeService {
    private final LawTypeRepository lawTypeRepository;
    private final LawRepository lawRepository;
    private final LawCommandService lawCommandService;
    private final ModelMapper modelMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public UUID createLawType(CreateLawTypeCommand command){
        log.info("Creating law type: {}", command.getName());

        // Check if law type name already exists
        if (lawTypeRepository.existsByName(command.getName())) {
            throw new IllegalArgumentException("Law type name already exists: " + command.getName());
        }

        LawTypeEntity lawType = LawTypeEntity.builder()
                .lawTypeId(UUID.randomUUID())
                .name(command.getName())
                .isDeleted(false)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        lawType = lawTypeRepository.save(lawType);
        publishLawTypeCreatedEvent(lawType);

        log.info("Law type created successfully with ID: {}", lawType.getLawTypeId());
        return lawType.getLawTypeId();
    }


    @Override
    public void updateLawType(UUID lawTypeId, UpdateLawTypeCommand command, UUID userId) {
        log.info("Updating law type: {}", lawTypeId);

        LawTypeEntity lawType = lawTypeRepository.findById(lawTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Law type not found: " + lawTypeId));

        if (command.getName() != null && !command.getName().equals(lawType.getName())) {
            if (lawTypeRepository.existsByName(command.getName())) {
                throw new IllegalArgumentException("Law type name already exists: " + command.getName());
            }
            lawType.setName(command.getName());
        }

        List<LawEntity> laws = lawRepository.findByLawType_LawTypeId(lawTypeId);
        for (LawEntity law : laws) {
            UpdateLawCommand updateLawCommand = new UpdateLawCommand();
            updateLawCommand.setLawTypeId(lawType.getLawTypeId());
            updateLawCommand.setUpdateByUserId(userId.toString());
            lawCommandService.updateLaw(UUID.fromString(law.getLawId()), updateLawCommand);
        }
        lawType.setUpdatedDate(LocalDateTime.now());
        lawTypeRepository.save(lawType);
        publishLawTypeUpdatedEvent(lawType);

        log.info("Law type updated successfully");
    }



    @Override
    public void deleteLawType(UUID lawTypeId) {
        log.info("Deleting law type: {}", lawTypeId);

        LawTypeEntity lawType = lawTypeRepository.findById(lawTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Law type not found: " + lawTypeId));
        long lawCount = lawRepository.countByLawType_LawTypeId(lawTypeId);
        boolean isHardDelete = false;

        if (lawCount > 0) {
            // Soft delete - laws are still using this type
            lawType.setIsDeleted(true);
            lawType.setUpdatedDate(LocalDateTime.now());
            lawTypeRepository.save(lawType);
            log.info("Law type soft deleted (has {} associated laws)", lawCount);
        } else {
            // Hard delete - no laws are using this type
            lawTypeRepository.delete(lawType);
            isHardDelete = true;
            log.info("Law type hard deleted (no associated laws)");
        }
        publishLawTypeDeletedEvent(lawType, isHardDelete);

        log.info("Law type deleted successfully");
    }


    private void publishLawTypeCreatedEvent(LawTypeEntity lawType) {
        LawTypeCreatedEvent event = LawTypeCreatedEvent.builder()
                .lawTypeId(lawType.getLawTypeId())
                .name(lawType.getName())
                .createdDate(lawType.getCreatedDate())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.LAW_TYPE_EXCHANGE,
                RabbitMQConfig.LAW_TYPE_CREATED_KEY,
                event
        );

        log.debug("Published LawTypeCreatedEvent for lawType: {}", lawType.getLawTypeId());
    }

    private void publishLawTypeUpdatedEvent(LawTypeEntity lawType) {
        LawTypeUpdatedEvent event = LawTypeUpdatedEvent.builder()
                .lawTypeId(lawType.getLawTypeId())
                .LawTypename(lawType.getName())
                .updatedDate(lawType.getUpdatedDate())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.LAW_TYPE_EXCHANGE,
                RabbitMQConfig.LAW_TYPE_UPDATED_KEY,
                event
        );


        log.debug("Published LawTypeUpdatedEvent for lawType: {}", lawType.getLawTypeId());
    }
    private void publishLawTypeDeletedEvent(LawTypeEntity lawType, boolean isHardDelete) {
        LawTypeDeletedEvent event = LawTypeDeletedEvent.builder()
                .lawTypeId(lawType.getLawTypeId())
                .name(lawType.getName())
                .isHardDelete(isHardDelete)
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.LAW_TYPE_EXCHANGE,
                RabbitMQConfig.LAW_TYPE_DELETED_KEY,
                event
        );

        log.debug("Published LawTypeDeletedEvent for lawType: {} [Hard Delete: {}]",
                lawType.getLawTypeId(), isHardDelete);
    }
}