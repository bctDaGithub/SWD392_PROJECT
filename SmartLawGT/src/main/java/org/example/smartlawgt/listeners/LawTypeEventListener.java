package org.example.smartlawgt.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.config.RabbitMQConfig;
import org.example.smartlawgt.events.LawType.LawTypeCreatedEvent;
import org.example.smartlawgt.events.LawType.LawTypeDeletedEvent;
import org.example.smartlawgt.events.LawType.LawTypeUpdatedEvent;
import org.example.smartlawgt.query.documents.LawTypeDocument;
import org.example.smartlawgt.query.repositories.LawMongoRepository;
import org.example.smartlawgt.query.repositories.LawTypeMongoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class LawTypeEventListener {
    private final LawMongoRepository lawMongoRepository;
    private final LawTypeMongoRepository lawTypeMongoRepository;
    @RabbitListener(queues = RabbitMQConfig.LAW_TYPE_CREATED_QUEUE)
    public void handleLawTypeCreatedEvent(LawTypeCreatedEvent event) {
        log.info("Received LawTypeCreatedEvent for lawType: {}", event.getLawTypeId());

        try {
            LawTypeDocument lawTypeDocument = LawTypeDocument.builder()
                    .lawTypeId(event.getLawTypeId().toString())
                    .lawTypename(event.getName())
                    .isDeleted(false)
                    .createdDate(event.getCreatedDate())
                    .updatedDate(event.getCreatedDate())
                    .build();

            lawTypeMongoRepository.save(lawTypeDocument);

            log.info("LawType document created in MongoDB successfully");

        } catch (Exception e) {
            log.error("Error processing LawTypeCreatedEvent", e);
            throw e;
        }
    }

    @RabbitListener(queues = RabbitMQConfig.LAW_TYPE_UPDATED_QUEUE)
    public void handleLawTypeUpdatedEvent(LawTypeUpdatedEvent event) {
        log.info("Received LawTypeUpdatedEvent for lawType: {}", event.getLawTypeId());

        try {
            lawTypeMongoRepository.findByLawTypeId(String.valueOf(event.getLawTypeId())).ifPresentOrElse(
                    lawTypeDocument -> {
                        lawTypeDocument.setLawTypename(event.getLawTypename());
                        lawTypeDocument.setUpdatedDate(event.getUpdatedDate());
                        lawTypeMongoRepository.save(lawTypeDocument);
                        log.info("LawType document updated in MongoDB");
                    },
                    () -> {
                        log.warn("LawType document not found in MongoDB: {}", event.getLawTypeId());
                    }
            );
        } catch (Exception e) {
            log.error("Error processing LawTypeUpdatedEvent", e);
            throw e;
        }
    }

    @RabbitListener(queues = RabbitMQConfig.LAW_TYPE_DELETED_QUEUE)
    public void handleLawTypeDeletedEvent(LawTypeDeletedEvent event) {
        log.info("Received LawTypeDeletedEvent for lawType: {}", event.getLawTypeId());

        try {
            if (Boolean.TRUE.equals(event.getIsHardDelete())) {
                // Hard delete - remove document
                lawTypeMongoRepository.deleteByLawTypeId(event.getLawTypeId().toString());
                log.info("LawType document deleted from MongoDB");
            } else {
                // Soft delete - mark as deleted
                lawTypeMongoRepository.findByLawTypeId(String.valueOf(event.getLawTypeId())).ifPresentOrElse(
                        lawTypeDocument -> {
                            lawTypeDocument.setIsDeleted(true);
                            lawTypeDocument.setUpdatedDate(LocalDateTime.now());

                            lawTypeMongoRepository.save(lawTypeDocument);
                            log.info("LawType document soft deleted in MongoDB");
                        },
                        () -> {
                            log.warn("LawType document not found for delete: {}", event.getLawTypeId());
                        }
                );
            }
        } catch (Exception e) {
            log.error("Error processing LawTypeDeletedEvent", e);
            throw e;
        }
    }
}


