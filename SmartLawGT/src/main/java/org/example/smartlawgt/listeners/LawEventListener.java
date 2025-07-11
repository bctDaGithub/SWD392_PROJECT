package org.example.smartlawgt.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.command.repositories.UserRepository;
import org.example.smartlawgt.config.RabbitMQConfig;
<<<<<<< Updated upstream
import org.example.smartlawgt.events.Law.LawCreatedEvent;
import org.example.smartlawgt.events.Law.LawDeletedEvent;
import org.example.smartlawgt.events.Law.LawUpdatedEvent;
=======
import org.example.smartlawgt.events.law.LawCreatedEvent;
import org.example.smartlawgt.events.law.LawDeletedEvent;
import org.example.smartlawgt.events.law.LawUpdatedEvent;
import org.example.smartlawgt.integration.export.service.LawExportService;
>>>>>>> Stashed changes
import org.example.smartlawgt.query.documents.LawDocument;
import org.example.smartlawgt.query.repositories.LawMongoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class LawEventListener {
    private final LawMongoRepository lawDocumentRepository;
    private final UserRepository userRepository;
    @RabbitListener(queues = RabbitMQConfig.LAW_CREATED_QUEUE)
    public void handleLawCreatedEvent(LawCreatedEvent event) {
        log.info("Received LawCreatedEvent for law: {}", event.getLawId());

        try {
            LawDocument lawDocument = LawDocument.builder()
                    .lawId(event.getLawId().toString())
                    .lawNumber(event.getLawNumber())
                    .lawTypeName(event.getLawTypeName())
                    .issueDate(event.getIssueDate())
                    .effectiveDate(event.getEffectiveDate())
                    .expiryDate(event.getExpiryDate())
                    .status(event.getStatus())
                    .issuingBody(event.getIssuingBody())
                    .contentUrl(event.getContentUrl())
                    .description(event.getDescription())
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();

            lawDocumentRepository.save(lawDocument);
            log.info("Law document created in MongoDB successfully");

        } catch (Exception e) {
            log.error("Error processing LawCreatedEvent", e);
            // Could implement retry logic or send to DLQ
            throw e;
        }
    }

    @RabbitListener(queues = RabbitMQConfig.LAW_UPDATED_QUEUE)
    public void handleLawUpdatedEvent(LawUpdatedEvent event) {
        log.info("Received LawUpdatedEvent for law: {}", event.getLawId());

        try {
            lawDocumentRepository.findById(event.getLawId().toString()).ifPresentOrElse(
                    lawDocument -> {
                        // Update all fields
                        lawDocument.setLawNumber(event.getLawNumber());
                        lawDocument.setLawTypeName(event.getLawTypeName());
                        lawDocument.setIssueDate(event.getIssueDate());
                        lawDocument.setEffectiveDate(event.getEffectiveDate());
                        lawDocument.setExpiryDate(event.getExpiryDate());
                        lawDocument.setStatus(event.getStatus());
                        lawDocument.setIssuingBody(event.getIssuingBody());
                        lawDocument.setContentUrl(event.getContentUrl());
                        lawDocument.setDescription(event.getDescription());
                        lawDocument.setUpdatedDate(LocalDateTime.now());

                        lawDocumentRepository.save(lawDocument);
                log.info("Law document updated in MongoDB");
            }, () -> {
                        log.warn("Law document not found in MongoDB: {}", event.getLawId());
                    }
            );
        } catch (Exception e) {
            log.error("Error processing LawUpdatedEvent", e);
            throw e;
        }
    }

    @RabbitListener(queues = RabbitMQConfig.LAW_DELETED_QUEUE)
    public void handleLawDeletedEvent(LawDeletedEvent event) {
        log.info("Received LawDeletedEvent for law: {}", event.getLawId());

        try {
            lawDocumentRepository.deleteById(event.getLawId().toString());
            log.info("Law document deleted from MongoDB");
        } catch (Exception e) {
            log.error("Error processing LawDeletedEvent", e);
            throw e;
        }
    }
}