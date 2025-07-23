package org.example.smartlawgt.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.command.entities.LawEntity;
import org.example.smartlawgt.command.entities.LawStatus;
import org.example.smartlawgt.command.repositories.LawRepository;
import org.example.smartlawgt.command.services.define.ILawCommandService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CheckExpireLawScheduler {
    private final LawRepository lawRepository;
    private final ILawCommandService lawCommandService;

    @Scheduled(cron = "0 0 0 * * *")
    public void checkExpireLaw(){
        List<LawEntity> lawEntities = lawRepository.findByExpiryDateBefore(LocalDateTime.now());
    for (LawEntity lawCheck : lawEntities) {
        if(lawCheck.getStatus() != LawStatus.EXPIRED){
            lawCommandService.changeLawStatus(UUID.fromString(lawCheck.getLawId()), LawStatus.EXPIRED );
        log.info("law: {} expired", lawCheck.getLawId());
        }
    }
    }
}
