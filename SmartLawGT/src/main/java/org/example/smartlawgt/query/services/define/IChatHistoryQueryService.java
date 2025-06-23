package org.example.smartlawgt.query.services.define;

import org.example.smartlawgt.query.documents.ChatHistoryDocument;

import java.util.List;
import java.util.UUID;

public interface IChatHistoryQueryService {
    List<ChatHistoryDocument> getChatHistoryByUserId(UUID userId);
}
