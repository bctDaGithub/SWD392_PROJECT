package org.example.smartlawgt.integration.ai.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.example.smartlawgt.query.documents.ChatHistoryDocument;
import org.example.smartlawgt.query.documents.UserPackageDocument;
import org.example.smartlawgt.query.repositories.ChatHistoryMongoRepository;
import org.example.smartlawgt.query.repositories.UserPackageMongoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiApiService {

    private final CustomDataService customDataService;
    private final ChatHistoryMongoRepository chatHistoryRepository;
    private final UserPackageMongoRepository userPackageMongoRepository;
    private final LawCacheService lawCacheService;
    private final OllamaService ollamaService;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();

    public String getGeminiResponse(String prompt, UUID userId) throws IOException {
        // 1. Check user package validity
        List<UserPackageDocument> activePackages = userPackageMongoRepository.findActiveSubscriptions(userId, LocalDateTime.now());

        if (activePackages.isEmpty()) {
            return "Bạn không có gói sử dụng hợp lệ để sử dụng AI.";
        }

        UserPackageDocument activePackage = activePackages.stream()
                .max(Comparator.comparingInt(UserPackageDocument::getDailyLimit))
                .orElse(null);

        int dailyLimit = activePackage.getDailyLimit();

        // 2. Check daily limit
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);

        long askedCountBefore = chatHistoryRepository.countByUserIdAndTimestampBetween(userId, start, end);
        if (askedCountBefore >= dailyLimit) {
            return "Bạn đã sử dụng hết số lượt hỏi hôm nay. Vui lòng quay lại vào ngày mai.";
        }

        // 3. Check cache first - NEW FEATURE
        String cachedAnswer = null;
        boolean usedCache = false;

        try {
            if (ollamaService.isAvailable()) {
                cachedAnswer = lawCacheService.findSimilarAnswer(prompt);
                if (cachedAnswer != null) {
                    usedCache = true;
                    log.info("Returning cached answer for user: {}", userId);
                }
            } else {
                log.warn("Ollama service not available, proceeding without cache lookup");
            }
        } catch (Exception e) {
            log.error("Error checking cache, proceeding with Gemini API", e);
        }

        String finalAnswer;

        if (usedCache && cachedAnswer != null) {
            // Use cached answer
            finalAnswer = cachedAnswer;
        } else {
            // 4. Get context from previous chat
            Optional<ChatHistoryDocument> lastChat = chatHistoryRepository.findTop1ByUserIdOrderByTimestampDesc(userId);

            String context = "";
            if (lastChat.isPresent()) {
                context = "Câu hỏi trước: " + lastChat.get().getQuestion() + "\n" +
                        "Trả lời trước: " + lastChat.get().getAnswer() + "\n";
            }

            // 5. Prepare prompt and call Gemini API
            String customData = customDataService.loadCustomData();
            String fullPrompt =
                    "Bạn là trợ lý pháp lý chuyên về luật giao thông Việt Nam cho hệ thống SmartLaw GT.\n\n" +
                    "DỮ LIỆU THAM KHẢO:\n" + customData + "\n\n" +
                    (context.isEmpty() ? "" : "NGỮ CẢNH TRƯỚC ĐÓ:\n" + context + "\n") +
                    "CÂU HỎI: " + prompt + "\n\n" +
                    "HƯỚNG DẪN TRẢ LỜI:\n" +
                    "1. Ưu tiên sử dụng dữ liệu tham khảo ở trên nếu có thông tin liên quan.\n" +
                    "2. Nếu dữ liệu tham khảo không đủ, BẮT BUỘC sử dụng kiến thức về luật giao thông Việt Nam của bạn để trả lời.\n" +
                    "3. Bạn có kiến thức về Nghị định 100/2019/NĐ-CP, Luật Giao thông đường bộ, và các quy định giao thông hiện hành của Việt Nam.\n" +
                    "4. Cung cấp thông tin cụ thể về mức phạt, điều luật, và quy định liên quan.\n" +
                    "5. CHỈ từ chối trả lời nếu câu hỏi hoàn toàn không liên quan đến giao thông.\n" +
                    "6. Trả lời bằng tiếng Việt, chi tiết và chính xác.\n" +
                    "7. Nếu không chắc chắn về số liệu cụ thể, hãy đưa ra thông tin tổng quát và khuyến nghị kiểm tra nguồn chính thức.\n\n" +
                    "Hãy trả lời ngay bây giờ:";

            // 6. Call Gemini API
            finalAnswer = callGeminiApi(fullPrompt);

            // 7. Cache the new Q&A if answer is valid - NEW FEATURE
            try {
                if (ollamaService.isAvailable() &&
                    !finalAnswer.contains("Không có thông tin nào liên quan") &&
                    !finalAnswer.contains("1900 1234")) {

                    // Extract clean answer (remove usage info)
                    String cleanAnswer = extractCleanAnswer(finalAnswer);
                    lawCacheService.cacheQuestionAnswer(prompt, cleanAnswer);
                    log.debug("Cached new Q&A for future use");
                }
            } catch (Exception e) {
                log.error("Error caching answer", e);
                // Continue without caching
            }
        }

        // 8. Save to chat history
        ChatHistoryDocument history = ChatHistoryDocument.builder()
                .userId(userId)
                .question(prompt)
                .answer(extractCleanAnswer(finalAnswer))
                .timestamp(LocalDateTime.now())
                .build();

        chatHistoryRepository.save(history);

        // 9. Calculate remaining usage
        long askedCountAfter = chatHistoryRepository.countByUserIdAndTimestampBetween(userId, start, end);
        int remaining = dailyLimit - (int) askedCountAfter;

        // 10. Add usage info to response
        String usageInfo = String.format("\n\n(Số lượt còn lại hôm nay: %d/%d)", remaining, dailyLimit);

        if (usedCache) {
            // For cached answers, the cache indicator is already included
            return finalAnswer + usageInfo;
        } else {
            return finalAnswer + usageInfo;
        }
    }

    /**
     * Call Gemini API
     */
    private String callGeminiApi(String fullPrompt) throws IOException {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String requestBody = String.format(
                "{\"contents\":[{\"role\":\"user\",\"parts\":[{\"text\":%s}]}]}",
                new ObjectMapper().writeValueAsString(fullPrompt)
        );

        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(JSON, requestBody))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            return root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();
        }
    }

    /**
     * Extract clean answer without usage info and cache indicators
     */
    private String extractCleanAnswer(String answer) {
        if (answer == null) return "";

        // Remove usage info pattern
        String clean = answer.replaceAll("\\n\\n\\(Số lượt còn lại hôm nay: \\d+/\\d+\\)$", "");

        // Remove cache indicator pattern
        clean = clean.replaceAll("\\n\\n\\[Từ cache - độ tương tự: \\d+\\.\\d+%\\]$", "");

        return clean.trim();
    }

    /**
     * Get cache statistics - NEW METHOD
     */
    public LawCacheService.CacheStats getCacheStats() {
        try {
            return lawCacheService.getCacheStats();
        } catch (Exception e) {
            log.error("Error getting cache stats", e);
            return new LawCacheService.CacheStats(0, 0, 0L);
        }
    }

    /**
     * Clear cache manually - NEW METHOD
     */
    public void clearCache() {
        try {
            lawCacheService.clearAllCache();
            log.info("Cache cleared manually");
        } catch (Exception e) {
            log.error("Error clearing cache", e);
        }
    }

    public boolean isUserOverDailyLimit(UUID userId, int dailyLimit) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);

        long count = chatHistoryRepository.countByUserIdAndTimestampBetween(userId, startOfDay, endOfDay);
        return count >= dailyLimit;
    }
}