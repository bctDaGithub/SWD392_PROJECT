package org.example.smartlawgt.integration.ai.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LawCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final OllamaService ollamaService;

    @Value("${ollama.similarity-threshold}")
    private double similarityThreshold;

    @Value("${cache.question.expiration-days}")
    private int cacheExpirationDays;

    @Value("${cache.data-version.key}")
    private String dataVersionKey;

    // Cache key patterns
    private static final String QUESTION_EMBEDDING_PREFIX = "question_embeddings:";
    private static final String QUESTION_ANSWER_PREFIX = "question_answers:";
    private static final String QUESTION_METADATA_PREFIX = "question_metadata:";

    /**
     * Cache question, answer and its embedding
     */
    public void cacheQuestionAnswer(String question, String answer) {
        try {
            String questionHash = DigestUtils.md5Hex(question);
            long dataVersion = getCurrentDataVersion();

            // Get embedding for the question
            double[] embedding = ollamaService.getEmbedding(question);

            // Cache embedding
            String embeddingKey = QUESTION_EMBEDDING_PREFIX + questionHash;
            redisTemplate.opsForValue().set(embeddingKey, embedding);
            redisTemplate.expire(embeddingKey, Duration.ofDays(cacheExpirationDays));

            // Cache answer
            String answerKey = QUESTION_ANSWER_PREFIX + questionHash;
            redisTemplate.opsForValue().set(answerKey, answer);
            redisTemplate.expire(answerKey, Duration.ofDays(cacheExpirationDays));

            // Cache metadata (question text, data version, timestamp)
            QuestionMetadata metadata = new QuestionMetadata(question, dataVersion, System.currentTimeMillis());
            String metadataKey = QUESTION_METADATA_PREFIX + questionHash;
            redisTemplate.opsForValue().set(metadataKey, metadata);
            redisTemplate.expire(metadataKey, Duration.ofDays(cacheExpirationDays));

            log.debug("Cached Q&A for question hash: {}", questionHash);

        } catch (Exception e) {
            log.error("Error caching question and answer", e);
        }
    }

    /**
     * Find similar cached answer based on embedding similarity
     */
    // Trong LawCacheService.findSimilarAnswer() method:
    public String findSimilarAnswer(String newQuestion) {
        try {
            if (!ollamaService.isAvailable()) {
                log.warn("Ollama service not available, skipping cache lookup");
                return null;
            }

            double[] newEmbedding = ollamaService.getEmbedding(newQuestion);
            Set<String> embeddingKeys = redisTemplate.keys(QUESTION_EMBEDDING_PREFIX + "*");

            if (embeddingKeys == null || embeddingKeys.isEmpty()) {
                log.debug("No cached embeddings found");
                return null;
            }

            double bestSimilarity = 0.0;
            String bestMatchHash = null;
            long currentTime = System.currentTimeMillis();
            long maxCacheAge = Duration.ofDays(cacheExpirationDays).toMillis();

            for (String embeddingKey : embeddingKeys) {
                try {
                    String questionHash = embeddingKey.replace(QUESTION_EMBEDDING_PREFIX, "");

                    Object metadataObj = redisTemplate.opsForValue()
                            .get(QUESTION_METADATA_PREFIX + questionHash);

                    if (metadataObj == null) {
                        removeQuestionFromCache(questionHash);
                        continue;
                    }

                    QuestionMetadata metadata = convertToQuestionMetadata(metadataObj);
                    if (metadata == null) {
                        removeQuestionFromCache(questionHash);
                        continue;
                    }

                    // Check cache age instead of data version
                    long cacheAge = currentTime - metadata.getTimestamp();
                    if (cacheAge > maxCacheAge) {
                        log.debug("Cache entry expired: {} days old",
                                Duration.ofMillis(cacheAge).toDays());
                        removeQuestionFromCache(questionHash);
                        continue;
                    }

                    Object embeddingObj = redisTemplate.opsForValue().get(embeddingKey);
                    if (embeddingObj == null) continue;

                    double[] cachedEmbedding = convertToDoubleArray(embeddingObj);
                    if (cachedEmbedding == null) continue;

                    double similarity = ollamaService.calculateCosineSimilarity(newEmbedding, cachedEmbedding);

                    // Thêm logging chi tiết để debug
                    log.debug("Comparing questions: '{}' vs cached '{}' - Similarity: {:.4f}",
                              newQuestion, metadata.getQuestionText(), similarity);

                    if (similarity > bestSimilarity && similarity >= similarityThreshold) {
                        log.info("New best match found: '{}' -> '{}' with similarity: {:.4f} (threshold: {})",
                                newQuestion, metadata.getQuestionText(), similarity, similarityThreshold);
                        bestSimilarity = similarity;
                        bestMatchHash = questionHash;
                    }

                } catch (Exception e) {
                    log.warn("Error processing cached embedding: {}", embeddingKey, e);
                }
            }

            if (bestMatchHash != null) {
                String answer = (String) redisTemplate.opsForValue()
                        .get(QUESTION_ANSWER_PREFIX + bestMatchHash);

                log.info("Found similar cached answer with similarity: {}", bestSimilarity);
                return answer;
            }

            log.debug("No similar cached answer found above threshold: {}", similarityThreshold);
            return null;

        } catch (Exception e) {
            log.error("Error finding similar answer", e);
            return null;
        }
    }

    /**
     * Clear all cached data (call when data.txt is completely rebuilt)
     */
    public void clearAllCache() {
        try {
            Set<String> allKeys = redisTemplate.keys("question_*");
            if (allKeys != null && !allKeys.isEmpty()) {
                redisTemplate.delete(allKeys);
                log.info("Cleared {} cached entries", allKeys.size());
            }
        } catch (Exception e) {
            log.error("Error clearing cache", e);
        }
    }

    /**
     * Update data version (call when data.txt changes)
     */
    public void updateDataVersion() {
        long newVersion = System.currentTimeMillis();
        redisTemplate.opsForValue().set(dataVersionKey, newVersion);
        log.info("Updated data version to: {}", newVersion);
    }

    /**
     * Get current data version
     */
    public long getCurrentDataVersion() {
        Object version = redisTemplate.opsForValue().get(dataVersionKey);
        return version != null ? (Long) version : 0L;
    }

    /**
     * Get cache statistics
     */
    public CacheStats getCacheStats() {
        try {
            Set<String> embeddingKeys = redisTemplate.keys(QUESTION_EMBEDDING_PREFIX + "*");
            Set<String> answerKeys = redisTemplate.keys(QUESTION_ANSWER_PREFIX + "*");

            int embeddingCount = embeddingKeys != null ? embeddingKeys.size() : 0;
            int answerCount = answerKeys != null ? answerKeys.size() : 0;
            long dataVersion = getCurrentDataVersion();

            return new CacheStats(embeddingCount, answerCount, dataVersion);

        } catch (Exception e) {
            log.error("Error getting cache stats", e);
            return new CacheStats(0, 0, 0L);
        }
    }

    // Private helper methods
    private void removeQuestionFromCache(String questionHash) {
        try {
            redisTemplate.delete(QUESTION_EMBEDDING_PREFIX + questionHash);
            redisTemplate.delete(QUESTION_ANSWER_PREFIX + questionHash);
            redisTemplate.delete(QUESTION_METADATA_PREFIX + questionHash);
        } catch (Exception e) {
            log.warn("Error removing question from cache: {}", questionHash, e);
        }
    }

    private QuestionMetadata convertToQuestionMetadata(Object metadataObj) {
        try {
            if (metadataObj instanceof byte[]) {
                byte[] metadataBytes = (byte[]) metadataObj;
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(metadataBytes, QuestionMetadata.class);
            } else if (metadataObj instanceof Map) {
                Map<String, Object> metadataMap = (Map<String, Object>) metadataObj;
                String questionText = (String) metadataMap.get("questionText");
                long dataVersion = ((Number) metadataMap.get("dataVersion")).longValue();
                long timestamp = ((Number) metadataMap.get("timestamp")).longValue();
                return new QuestionMetadata(questionText, dataVersion, timestamp);
            }
        } catch (Exception e) {
            log.warn("Error converting metadata object: {}", metadataObj, e);
        }
        return null;
    }

    private double[] convertToDoubleArray(Object obj) {
        try {
            if (obj instanceof double[]) {
                return (double[]) obj;
            } else if (obj instanceof ArrayList) {
                @SuppressWarnings("unchecked")
                ArrayList<Number> arrayList = (ArrayList<Number>) obj;
                double[] doubleArray = new double[arrayList.size()];
                for (int i = 0; i < arrayList.size(); i++) {
                    doubleArray[i] = arrayList.get(i).doubleValue();
                }
                return doubleArray;
            } else if (obj instanceof byte[]) {
                byte[] byteArray = (byte[]) obj;
                double[] doubleArray = new double[byteArray.length / 8];
                for (int i = 0; i < doubleArray.length; i++) {
                    long bits = 0;
                    for (int j = 0; j < 8; j++) {
                        bits <<= 8;
                        bits |= (byteArray[i * 8 + j] & 0xff);
                    }
                    doubleArray[i] = Double.longBitsToDouble(bits);
                }
                return doubleArray;
            }
        } catch (Exception e) {
            log.warn("Error converting to double array: {}", obj, e);
        }
        return null;
    }

    // Inner classes for data structures
    public static class QuestionMetadata {
        private String questionText;
        private long dataVersion;
        private long timestamp;

        public QuestionMetadata() {}

        public QuestionMetadata(String questionText, long dataVersion, long timestamp) {
            this.questionText = questionText;
            this.dataVersion = dataVersion;
            this.timestamp = timestamp;
        }

        // Getters and setters
        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public long getDataVersion() { return dataVersion; }
        public void setDataVersion(long dataVersion) { this.dataVersion = dataVersion; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class CacheStats {
        private final int embeddingCount;
        private final int answerCount;
        private final long dataVersion;

        public CacheStats(int embeddingCount, int answerCount, long dataVersion) {
            this.embeddingCount = embeddingCount;
            this.answerCount = answerCount;
            this.dataVersion = dataVersion;
        }

        public int getEmbeddingCount() { return embeddingCount; }
        public int getAnswerCount() { return answerCount; }
        public long getDataVersion() { return dataVersion; }
    }
}