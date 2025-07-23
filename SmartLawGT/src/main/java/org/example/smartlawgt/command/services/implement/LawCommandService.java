package org.example.smartlawgt.command.services.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.command.dtos.CreateLawCommand;
import org.example.smartlawgt.command.dtos.UpdateLawCommand;
import org.example.smartlawgt.command.entities.LawEntity;
import org.example.smartlawgt.command.entities.LawStatus;
import org.example.smartlawgt.command.entities.LawTypeEntity;
import org.example.smartlawgt.command.entities.UserEntity;
import org.example.smartlawgt.command.repositories.LawRepository;
import org.example.smartlawgt.command.repositories.LawTypeRepository;
import org.example.smartlawgt.command.repositories.UserRepository;
import org.example.smartlawgt.command.services.define.ILawCommandService;
import org.example.smartlawgt.config.RabbitMQConfig;
import org.example.smartlawgt.events.Law.LawCreatedEvent;
import org.example.smartlawgt.events.Law.LawDeletedEvent;
import org.example.smartlawgt.events.Law.LawUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LawCommandService implements ILawCommandService {
    private final LawRepository lawRepository;
    private final LawTypeRepository lawTypeRepository;
    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;
    private final NotificationCommandService notificationCommandService;

    @Override
    public UUID createLaw(CreateLawCommand command){
        log.info("Creating law with number: {}", command.getLawNumber());
        if (lawRepository.existsByLawNumber(command.getLawNumber())){
            throw new IllegalArgumentException("Law number already exists: " + command.getLawNumber());
        }
        Optional<UserEntity> userCreated = userRepository.findById(command.getCreatedByUserId());
        // Get related entities
        LawTypeEntity lawType = lawTypeRepository.findById(command.getLawTypeId())

                .orElseThrow(() -> new IllegalArgumentException("Law type not found: " + command.getLawTypeId()));


        // Create law entity
        LawEntity law = LawEntity.builder().lawId(UUID.randomUUID().toString())
                .lawType(lawType)
                .lawNumber(command.getLawNumber())
                .issueDate(command.getIssueDate())
                .effectiveDate(command.getEffectiveDate())
                .expiryDate(command.getExpiryDate())
                .status(LawStatus.valueOf(command.getStatus().toUpperCase()))
                .issuingBody(command.getIssuingBody())
                .contentUrl(command.getContentUrl())
                .description(command.getDescription())
                .createdBy(userCreated.get())
                .build();

        law = lawRepository.save(law);

        // Publish event to RabbitMQ
        publishLawCreatedEvent(law);
        if(law.getStatus().equals(LawStatus.VALID)) {
            notificationCommandService.notifyLawCreated(law.getLawNumber());
        }
        //send notification
        sendBroadcastNotification(law, "created");

        log.info("Law created successfully with ID: {}", law.getLawId());
        return UUID.fromString(law.getLawId());
        }

    @Override
    public void updateLaw(UUID lawId, UpdateLawCommand command) {
        log.info("Updating law: {}", lawId);

      //  Optional<UserEntity> userUpdated = userRepository.findById(UUID.fromString(command.getUpdateByUserId()));
        LawEntity law = lawRepository.findById(lawId.toString())
                .orElseThrow(() -> new IllegalArgumentException("Law not found: " + lawId));
        String oldLawTypeId = String.valueOf(law.getLawType().getLawTypeId());

        // Update fields if provided
        if (command.getLawTypeId() != null) {
            LawTypeEntity lawType = lawTypeRepository.findById(command.getLawTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Law type not found: " + command.getLawTypeId()));
            law.setLawType(lawType);
        }

        if (command.getLawNumber() != null && !command.getLawNumber().equals(law.getLawNumber())) {
            if (lawRepository.existsByLawNumber(command.getLawNumber())) {
                throw new IllegalArgumentException("Law number already exists: " + command.getLawNumber());
            }
            law.setLawNumber(command.getLawNumber());
        }

        if (command.getIssueDate() != null) {
            law.setIssueDate(command.getIssueDate());
        }

        if (command.getEffectiveDate() != null) {
            law.setEffectiveDate(command.getEffectiveDate());
        }

        if (command.getExpiryDate() != null) {
            law.setExpiryDate(command.getExpiryDate());
        }

        if (command.getStatus() != null) {
            law.setStatus(LawStatus.valueOf(command.getStatus().toUpperCase()));
        }

        if (command.getIssuingBody() != null) {
            law.setIssuingBody(command.getIssuingBody());
        }

        if (command.getContentUrl() != null) {
            law.setContentUrl(command.getContentUrl());
        }

        if (command.getDescription() != null) {
            law.setDescription(command.getDescription());
        }
       law.setUpdatedBy(UUID.fromString(command.getUpdateByUserId()));
      //  law.setUpdatedBy(userUpdated.get());
        law = lawRepository.save(law);

        // Publish event to RabbitMQ
        publishLawUpdatedEvent(law, oldLawTypeId);
    //hmmmmmm
if(law.getStatus().equals(LawStatus.VALID)) {
    notificationCommandService.notifyLawUpdated(law.getLawNumber());
}
        //send notification
        sendBroadcastNotification(law, "updated");

        log.info("Law updated successfully");
    }

    @Override
    public void deleteLaw(UUID lawId) {
        log.info("Deleting law: {}", lawId);

        LawEntity law = lawRepository.findById(String.valueOf(lawId))
                .orElseThrow(() -> new IllegalArgumentException("Law not found: " + lawId));

       // String lawNumber = law.getLawNumber();

        lawRepository.delete(law);

        // Publish event to RabbitMQ
        publishLawDeletedEvent(law);

        log.info("Law deleted successfully");
    }

    @Override
    public void changeLawStatus(UUID lawId, LawStatus status) {
        log.info("Changing law status: {} to {}", lawId, status);

        LawEntity law = lawRepository.findById(String.valueOf(lawId))
                .orElseThrow(() -> new IllegalArgumentException("Law not found: " + lawId));

        law.setStatus(status);
      //  law.setUpdatedBy(userUpdated.get());
        law = lawRepository.save(law);

        // Publish event to RabbitMQ
        publishLawUpdatedEvent(law, String.valueOf(law.getLawType().getLawTypeId()));
if (law.getStatus().equals(LawStatus.VALID)) {
    notificationCommandService.notifyLawUpdated(law.getLawNumber());
}

        log.info("Law status changed successfully");
    }

        //===============================private=====================================//
    private void publishLawCreatedEvent(LawEntity law) {
        LawCreatedEvent event = LawCreatedEvent.builder()
                .lawId(UUID.fromString(law.getLawId()))
                .lawNumber(law.getLawNumber())
                .lawTypeId(law.getLawType().getLawTypeId())
                .lawTypeName(law.getLawType().getName())
                .status(law.getStatus().toString())
                .issueDate(law.getIssueDate())
                .issuingBody(law.getIssuingBody())
                .effectiveDate(law.getEffectiveDate())
                .expiryDate(law.getExpiryDate())
                .description(law.getDescription())
                .contentUrl(law.getContentUrl())
                .CreatedByUserId(String.valueOf(law.getCreatedBy().getUserId()))
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.LAW_EXCHANGE,
                RabbitMQConfig.LAW_CREATED_KEY,
                event,
                message -> {
                    message.getMessageProperties().setContentType("application/json");
                    message.getMessageProperties().setContentEncoding("UTF-8");
                    return message;
                }
        );
    }

    private void publishLawUpdatedEvent(LawEntity law, String oldLawTypeId) {
        LawUpdatedEvent event = LawUpdatedEvent.builder()
                .lawId(UUID.fromString(law.getLawId()))
                .lawNumber(law.getLawNumber())
                .lawTypeId(String.valueOf(law.getLawType().getLawTypeId()))
                .oldLawTypeId(oldLawTypeId)
                .lawTypeName(law.getLawType().getName())
                .issueDate(law.getIssueDate())
                .effectiveDate(law.getEffectiveDate())
                .expiryDate(law.getExpiryDate())
                .status(law.getStatus().toString())
                .issuingBody(law.getIssuingBody())
                .contentUrl(law.getContentUrl())
                .description(law.getDescription())
                .UpdatedByUserId(String.valueOf(law.getUpdatedBy()))
                .build();


        rabbitTemplate.convertAndSend(
                RabbitMQConfig.LAW_EXCHANGE,
                RabbitMQConfig.LAW_UPDATED_KEY,
                event
        );
    }

    private void publishLawDeletedEvent(LawEntity law) {
        LawDeletedEvent event = LawDeletedEvent.builder()
                .lawId(UUID.fromString(law.getLawId()))
                .lawNumber(law.getLawNumber())
                .lawTypeId(law.getLawType().getLawTypeId())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.LAW_EXCHANGE,
                RabbitMQConfig.LAW_DELETED_KEY,
                event
        );
    }

    //=========================================================================================//
    private void sendBroadcastNotification(LawEntity law, String action) {
        String title = String.format("Law %s", action);
        String content = String.format(
                "Law %s - %s has been %s. Effective from %s",
                law.getLawNumber(),
                law.getDescription(),
                action,
                law.getEffectiveDate()
        );

        // send broadcast notification
     /*   notificationService.sendBroadcastNotification(
                title,
                content,
                NotificationType.LAW_UPDATE
        );

        log.info("Broadcast notification sent for law {}", law.getLawNumber());
    }
    */
    }

}


