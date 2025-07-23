package org.example.smartlawgt.integration.ai.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OllamaService {

    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${ollama.embedding-model}")
    private String embeddingModel;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final MediaType JSON = MediaType.get("application/json");

    /**
     * Get embedding vector from Ollama for a given text
     */
    public double[] getEmbedding(String text) {
        try {
            String requestBody = String.format(
                    "{\"model\":\"%s\",\"prompt\":\"%s\"}",
                    embeddingModel,
                    text.replace("\"", "\\\"").replace("\n", "\\n")
            );

            Request request = new Request.Builder()
                    .url(ollamaBaseUrl + "/api/embeddings")
                    .post(RequestBody.create(JSON, requestBody)) // ← FIX: Đổi thứ tự parameters
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Ollama API call failed: {}", response.code());
                    throw new RuntimeException("Failed to get embedding from Ollama");
                }

                String responseBody = response.body().string();
                JsonNode root = objectMapper.readTree(responseBody);
                JsonNode embeddingNode = root.get("embedding");

                if (embeddingNode == null || !embeddingNode.isArray()) {
                    throw new RuntimeException("Invalid embedding response from Ollama");
                }

                double[] embedding = new double[embeddingNode.size()];
                for (int i = 0; i < embeddingNode.size(); i++) {
                    embedding[i] = embeddingNode.get(i).asDouble();
                }

                log.debug("Generated embedding with {} dimensions for text length: {}",
                        embedding.length, text.length());
                return embedding;
            }
        } catch (Exception e) {
            log.error("Error getting embedding from Ollama", e);
            throw new RuntimeException("Failed to get embedding: " + e.getMessage(), e);
        }
    }

    public double calculateCosineSimilarity(double[] vectorA, double[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vector dimensions must match");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public boolean isAvailable() {
        try {
            Request request = new Request.Builder()
                    .url(ollamaBaseUrl + "/api/tags")
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            log.warn("Ollama service not available: {}", e.getMessage());
            return false;
        }
    }
}