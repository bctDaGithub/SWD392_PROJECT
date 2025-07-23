package org.example.smartlawgt.integration.ai.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.integration.ai.services.CustomDataService;
import org.example.smartlawgt.integration.ai.services.GeminiApiService;
import org.example.smartlawgt.integration.ai.services.OllamaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/cache")
@RequiredArgsConstructor
@Slf4j
public class CacheManagementController {

    private final CustomDataService customDataService;
    private final GeminiApiService geminiApiService;
    private final OllamaService ollamaService;

    /**
     * Get cache and data file status
     */
    @GetMapping("/status")
    public ResponseEntity<?> getCacheStatus() {
        try {
            CustomDataService.DataFileInfo info = customDataService.getDataFileInfo();
            boolean ollamaAvailable = ollamaService.isAvailable();
            boolean dataOutOfSync = customDataService.isDataOutOfSync();

            return ResponseEntity.ok(Map.of(
                "fileExists", info.isExists(),
                "fileSize", info.getFormattedSize(),
                "lineCount", info.getLineCount(),
                "lastModified", info.getFormattedLastModified(),
                "cacheStats", info.getCacheStats(),
                "ollamaAvailable", ollamaAvailable,
                "dataOutOfSync", dataOutOfSync
            ));
        } catch (Exception e) {
            log.error("Error getting cache status", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Manual cache clear (emergency use)
     */
    @PostMapping("/clear")
    public ResponseEntity<?> clearCache() {
        try {
            customDataService.clearAllCaches();
            return ResponseEntity.ok(Map.of("message", "Cache cleared successfully"));
        } catch (Exception e) {
            log.error("Error clearing cache", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Force sync cache with data file
     */
    @PostMapping("/sync")
    public ResponseEntity<?> syncCache() {
        try {
            customDataService.syncCacheWithDataFile();
            return ResponseEntity.ok(Map.of("message", "Cache synced successfully"));
        } catch (Exception e) {
            log.error("Error syncing cache", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}