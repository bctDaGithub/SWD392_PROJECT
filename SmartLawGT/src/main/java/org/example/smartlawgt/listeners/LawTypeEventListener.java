package org.example.smartlawgt.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.config.RabbitMQConfig;
import org.example.smartlawgt.events.Law.LawCreatedEvent;
import org.example.smartlawgt.events.Law.LawDeletedEvent;
import org.example.smartlawgt.events.Law.LawUpdatedEvent;
import org.example.smartlawgt.events.law_type.LawTypeCreatedEvent;
import org.example.smartlawgt.events.law_type.LawTypeDeletedEvent;
import org.example.smartlawgt.events.law_type.LawTypeUpdatedCountEvent;
import org.example.smartlawgt.events.law_type.LawTypeUpdatedEvent;
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
                    .lawCount(0L)
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

    //===================================================//

    @RabbitListener(queues = RabbitMQConfig.LAW_CREATED_COUNT_QUEUE)
    public void handleLawCreated(LawCreatedEvent event) {
        log.info("Processing LawCreatedEvent for lawType: {}", event.getLawTypeId());

        try {
            lawTypeMongoRepository.findByLawTypeId(event.getLawTypeId().toString())
                    .ifPresentOrElse(
                            lawType -> {
                                // Increment count
                                Long currentCount = lawType.getLawCount() != null ? lawType.getLawCount() : 0L;
                                lawType.setLawCount(currentCount + 1);

                                lawTypeMongoRepository.save(lawType);
                                log.info("Law count incremented to {} for lawType: {}",
                                        lawType.getLawCount(), event.getLawTypeId());
                            },
                            () -> log.warn("LawType not found in MongoDB: {}", event.getLawTypeId())
                    );
        } catch (Exception e) {
            log.error("Error processing LawCreatedEvent for lawType: {}", event.getLawTypeId(), e);
        }

    }
    @RabbitListener(queues = RabbitMQConfig.LAW_DELETED_COUNT_QUEUE)
    public void handleLawDeleted(LawDeletedEvent event) {

        try {
            lawTypeMongoRepository.findByLawTypeId(event.getLawTypeId().toString())
                    .ifPresentOrElse(
                            lawType -> {
                                // Decrement count (ensure it doesn't go below 0)
                                Long currentCount = lawType.getLawCount() != null ? lawType.getLawCount() : 0L;
                                if (currentCount > 0) {
                                    lawType.setLawCount(currentCount - 1);
                                    lawTypeMongoRepository.save(lawType);
                                    log.info("Law count decremented to {} for lawType: {}",
                                            lawType.getLawCount(), event.getLawTypeId());
                                } else {
                                    log.warn("Cannot decrement law count below 0 for lawType: {}",
                                            event.getLawTypeId());
                                }
                            },
                            () -> log.warn("LawType not found in MongoDB: {}", event.getLawTypeId())
                    );
        } catch (Exception e) {
            log.error("Error processing LawDeletedEvent for lawType: {}", event.getLawTypeId(), e);
        }
    }
    @RabbitListener(queues = RabbitMQConfig.LAW_UPDATED_COUNT_QUEUE)
    public void handleLawUpdated(LawUpdatedEvent event) {
        log.info("Processing LawUpdatedEvent for law: {}", event.getLawId());

        try {
            // Only process if oldLawTypeId is provided and different from new one
            if (event.getOldLawTypeId() != null &&
                    !event.getLawTypeId().equals(event.getOldLawTypeId().toString())) {

                log.info("Law {} changed type from {} to {}",
                        event.getLawId(), event.getOldLawTypeId(), event.getLawTypeId());

                // Decrement count for old lawType
                lawTypeMongoRepository.findByLawTypeId(event.getOldLawTypeId().toString())
                        .ifPresent(oldLawType -> {
                            Long currentCount = oldLawType.getLawCount() != null ?
                                    oldLawType.getLawCount() : 0L;
                            if (currentCount > 0) {
                                oldLawType.setLawCount(currentCount - 1);
                                lawTypeMongoRepository.save(oldLawType);
                                log.info("Decremented count to {} for old lawType: {}",
                                        oldLawType.getLawCount(), event.getOldLawTypeId());
                            }
                        });

                // Increment count for new lawType
                lawTypeMongoRepository.findByLawTypeId(event.getLawTypeId())
                        .ifPresent(newLawType -> {
                            Long currentCount = newLawType.getLawCount() != null ?
                                    newLawType.getLawCount() : 0L;
                            newLawType.setLawCount(currentCount + 1);
                            lawTypeMongoRepository.save(newLawType);
                            log.info("Incremented count to {} for new lawType: {}",
                                    newLawType.getLawCount(), event.getLawTypeId());
                        });
            }
        } catch (Exception e) {
            log.error("Error processing LawUpdatedEvent for law: {}", event.getLawId(), e);
        }
    }

}


