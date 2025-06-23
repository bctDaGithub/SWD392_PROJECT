package org.example.smartlawgt.query.services.implement;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.query.documents.ChatHistoryDocument;
import org.example.smartlawgt.query.repositories.ChatHistoryMongoRepository;
import org.example.smartlawgt.query.services.define.IChatHistoryQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatHistoryQueryService implements IChatHistoryQueryService {

    private final ChatHistoryMongoRepository repository;

    @Override
    public List<ChatHistoryDocument> getChatHistoryByUserId(UUID userId) {
        return repository.findByUserIdOrderByTimestampDesc(userId);
    }
}
