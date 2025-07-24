package org.example.smartlawgt.integration.ai.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomDataService {

    private final LawCacheService lawCacheService;

    private static final String DATA_FILE_PATH = "src/main/resources/data.txt";

    // Cache để tránh đọc file liên tục
    private String cachedData = null;
    private long lastModifiedTime = 0;

    /**
     * Load custom data với cache file system
     */
    public String loadCustomData() throws IOException {
        try {
            Path dataPath = Paths.get(DATA_FILE_PATH);

            if (!Files.exists(dataPath)) {
                log.warn("Data file not found: {}", DATA_FILE_PATH);
                return "";
            }

            BasicFileAttributes attrs = Files.readAttributes(dataPath, BasicFileAttributes.class);
            long currentModifiedTime = attrs.lastModifiedTime().toMillis();

            // Kiểm tra nếu file đã thay đổi
            if (cachedData == null || currentModifiedTime != lastModifiedTime) {
                log.debug("Loading data file - last modified: {}",
                         Instant.ofEpochMilli(currentModifiedTime));

                cachedData = new String(Files.readAllBytes(dataPath));
                lastModifiedTime = currentModifiedTime;

                log.info("Loaded data file: {} bytes", cachedData.length());
            }

            return cachedData;

        } catch (IOException e) {
            log.error("Error loading custom data", e);
            throw new IOException("Failed to load custom data: " + e.getMessage(), e);
        }
    }

    /**
     * Call this when data.txt is completely rebuilt (not appended)
     * This happens when the file structure changes or content is rewritten
     */
    public void onDataRebuilt() {
        try {
            log.info("Data file was rebuilt - clearing all cache");

            // Clear Redis cache completely
            lawCacheService.clearAllCache();

            // Update data version
            lawCacheService.updateDataVersion();

            // Clear local file cache
            clearLocalCache();

            log.info("Cache cleared due to data rebuild");

        } catch (Exception e) {
            log.error("Error handling data rebuild", e);
        }
    }

    /**
     * Call this when new laws are appended to data.txt
     * Cache can remain valid as existing content hasn't changed
     */
    public void onDataAppended() {
        try {
            log.info("Data file was appended - preserving cache");

            // CHỈ clear local file cache để load content mới
            // KHÔNG update data version để giữ cache cũ
            clearLocalCache();

            log.info("Local cache cleared, Redis cache preserved");

        } catch (Exception e) {
            log.error("Error handling data append", e);
        }
    }

    /**
     * Manual cache clear - for admin operations
     */
    public void clearAllCaches() {
        try {
            log.info("Manual cache clear requested");

            lawCacheService.clearAllCache();
            clearLocalCache();

            log.info("All caches cleared manually");

        } catch (Exception e) {
            log.error("Error clearing caches manually", e);
        }
    }

    /**
     * Get file information and cache statistics
     */
    public DataFileInfo getDataFileInfo() {
        try {
            Path dataPath = Paths.get(DATA_FILE_PATH);

            if (!Files.exists(dataPath)) {
                return new DataFileInfo(false, 0, 0, 0,
                                      lawCacheService.getCacheStats());
            }

            long size = Files.size(dataPath);
            long lines = Files.lines(dataPath).count();
            long lastModified = Files.getLastModifiedTime(dataPath).toMillis();

            return new DataFileInfo(true, size, lines, lastModified,
                                  lawCacheService.getCacheStats());

        } catch (Exception e) {
            log.error("Error getting data file info", e);
            return new DataFileInfo(false, 0, 0, 0,
                                  new LawCacheService.CacheStats(0, 0, 0L));
        }
    }

    /**
     * Check if data file has been modified since last cache update
     */
    public boolean isDataOutOfSync() {
        try {
            Path dataPath = Paths.get(DATA_FILE_PATH);

            if (!Files.exists(dataPath)) {
                return false;
            }

            long fileModifiedTime = Files.getLastModifiedTime(dataPath).toMillis();
            long cacheDataVersion = lawCacheService.getCurrentDataVersion();

            // If cache version is older than file modification
            return cacheDataVersion < fileModifiedTime;

        } catch (Exception e) {
            log.error("Error checking data sync", e);
            return true; // Assume out of sync on error
        }
    }

    /**
     * Sync cache with current data file state
     */
    public void syncCacheWithDataFile() {
        try {
            if (isDataOutOfSync()) {
                log.info("Data is out of sync - updating cache");
                onDataRebuilt();
            } else {
                log.debug("Data is in sync with cache");
            }
        } catch (Exception e) {
            log.error("Error syncing cache with data file", e);
        }
    }

    /**
     * Get data file last modified time
     */
    public long getDataFileLastModified() {
        try {
            Path dataPath = Paths.get(DATA_FILE_PATH);
            return Files.exists(dataPath) ?
                   Files.getLastModifiedTime(dataPath).toMillis() : 0;
        } catch (Exception e) {
            log.error("Error getting file modification time", e);
            return 0;
        }
    }

    // Private helper methods

    private void clearLocalCache() {
        cachedData = null;
        lastModifiedTime = 0;
        log.debug("Local file cache cleared");
    }

    // Inner class for comprehensive data file information
    public static class DataFileInfo {
        private final boolean exists;
        private final long sizeInBytes;
        private final long lineCount;
        private final long lastModifiedTime;
        private final LawCacheService.CacheStats cacheStats;

        public DataFileInfo(boolean exists, long sizeInBytes, long lineCount,
                           long lastModifiedTime, LawCacheService.CacheStats cacheStats) {
            this.exists = exists;
            this.sizeInBytes = sizeInBytes;
            this.lineCount = lineCount;
            this.lastModifiedTime = lastModifiedTime;
            this.cacheStats = cacheStats;
        }

        // Getters
        public boolean isExists() { return exists; }
        public long getSizeInBytes() { return sizeInBytes; }
        public long getLineCount() { return lineCount; }
        public long getLastModifiedTime() { return lastModifiedTime; }
        public LawCacheService.CacheStats getCacheStats() { return cacheStats; }

        public String getFormattedSize() {
            if (sizeInBytes < 1024) return sizeInBytes + " B";
            if (sizeInBytes < 1024 * 1024) return String.format("%.2f KB", sizeInBytes / 1024.0);
            return String.format("%.2f MB", sizeInBytes / (1024.0 * 1024));
        }

        public String getFormattedLastModified() {
            return Instant.ofEpochMilli(lastModifiedTime).toString();
        }

        public boolean isCacheOutOfSync() {
            return lastModifiedTime > cacheStats.getDataVersion();
        }
    }
}