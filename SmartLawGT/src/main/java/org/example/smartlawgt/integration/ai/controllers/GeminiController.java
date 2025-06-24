package org.example.smartlawgt.integration.ai.controllers;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.integration.ai.services.GeminiApiService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiApiService geminiApiService;

    @PostMapping("/ask")
    public String askGemini(@RequestParam String question, @RequestParam UUID userId) throws IOException {
        return geminiApiService.getGeminiResponse(question, userId);
    }
}