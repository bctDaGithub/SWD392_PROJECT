package org.example.smartlawgt.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.query.repositories.ChatHistoryMongoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatCleanupScheduler {

    private final ChatHistoryMongoRepository chatHistoryRepository;

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanOldChats() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        long deletedCount = chatHistoryRepository.deleteByTimestampBefore(oneMonthAgo);
        log.info("Đã xóa {} đoạn chat cũ hơn 1 tháng.", deletedCount);
    }
}