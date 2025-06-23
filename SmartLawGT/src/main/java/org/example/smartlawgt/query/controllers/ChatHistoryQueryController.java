package org.example.smartlawgt.query.controllers;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.query.documents.ChatHistoryDocument;
import org.example.smartlawgt.query.services.define.IChatHistoryQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.query-path}/chat-history")
@RequiredArgsConstructor
public class ChatHistoryQueryController {

    private final IChatHistoryQueryService service;

    @GetMapping("/{userId}")
    public ResponseEntity<List<ChatHistoryDocument>> getChatHistory(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.getChatHistoryByUserId(userId));
    }
}
