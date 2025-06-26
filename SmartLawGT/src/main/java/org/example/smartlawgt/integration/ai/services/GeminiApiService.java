package org.example.smartlawgt.integration.ai.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeminiApiService {

    private final CustomDataService customDataService;
    private final ChatHistoryMongoRepository chatHistoryRepository;
    private final UserPackageMongoRepository userPackageMongoRepository;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();

    public String getGeminiResponse(String prompt, UUID userId) throws IOException {
        // 1. Lấy gói còn hiệu lực
        List<UserPackageDocument> activePackages = userPackageMongoRepository.findActiveSubscriptions(userId, LocalDateTime.now());

        if (activePackages.isEmpty()) {
            return "Bạn không có gói sử dụng hợp lệ để sử dụng AI.";
        }

        // 2. Ưu tiên gói có dailyLimit cao nhất
        UserPackageDocument activePackage = activePackages.stream()
                .max(Comparator.comparingInt(UserPackageDocument::getDailyLimit))
                .orElse(null);

        int dailyLimit = activePackage.getDailyLimit();

        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);

        long askedCountBefore = chatHistoryRepository.countByUserIdAndTimestampBetween(userId, start, end);
        if (askedCountBefore >= dailyLimit) {
            return "Bạn đã sử dụng hết số lượt hỏi hôm nay. Vui lòng quay lại vào ngày mai.";
        }

        // 4. Gửi prompt cho Gemini
        String customData = customDataService.loadCustomData();
        String fullPrompt = "Dựa trên thông tin sau:\n" + customData +
                "\nNếu câu hỏi không liên quan đến thông tin trên, hãy trả lời là không có thông tin nào liên quan." +
                "\nTrả lời câu hỏi bằng tiếng Việt: " + prompt;

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String requestBody = String.format(
                "{\"contents\":[{\"role\":\"user\",\"parts\":[{\"text\":%s}]}]}",
                new ObjectMapper().writeValueAsString(fullPrompt)
        );

        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            String answerText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            ChatHistoryDocument history = ChatHistoryDocument.builder()
                    .userId(userId)
                    .question(prompt)
                    .answer(answerText)
                    .timestamp(LocalDateTime.now())
                    .build();

            chatHistoryRepository.save(history);

            long askedCountAfter = chatHistoryRepository.countByUserIdAndTimestampBetween(userId, start, end);
            int remaining = dailyLimit - (int) askedCountAfter;

            return answerText + "\n\n(Số lượt còn lại hôm nay: " + remaining + "/" + dailyLimit + ")";
        }
    }

    public boolean isUserOverDailyLimit(UUID userId, int dailyLimit) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);

        long count = chatHistoryRepository.countByUserIdAndTimestampBetween(userId, startOfDay, endOfDay);
        return count >= dailyLimit;
    }
}
