package org.example.smartlawgt.integration.export.controller;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.integration.export.service.LawExportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/query/export/laws")
@RequiredArgsConstructor
public class LawExportController {
    private final LawExportService lawExportService;

    @PostMapping("/append-all")
    // @PreAuthorize("hasRole('ADMIN')") waiting jwt :v
    public ResponseEntity<?> appendAllLaws() {
        try {
            String filePath = lawExportService.appendAllLaws();
            LawExportService.FileInfo info = lawExportService.getFileInfo();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Laws appended successfully",
                    "filePath", filePath,
                    "fileSize", info.getFormattedSize(),
                    "totalLines", info.lineCount
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // Append laws by type
    @PostMapping("/append-by-type")
    //@PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> appendByType(@RequestParam String lawType) {
        try {
            String filePath = lawExportService.appendLawsByType(lawType);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Laws of type '" + lawType + "' appended successfully",
                    "filePath", filePath
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // Append single law
    @PostMapping("/append-single/{lawId}")
    //@PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> appendSingleLaw(@PathVariable String lawId) {
        try {
            String filePath = lawExportService.appendSingleLaw(lawId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Law appended successfully",
                    "filePath", filePath
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // Get file info
    @GetMapping("/file-info")
    //@PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> getFileInfo() {
        LawExportService.FileInfo info = lawExportService.getFileInfo();

        return ResponseEntity.ok(Map.of(
                "filePath", info.path,
                "size", info.getFormattedSize(),
                "sizeInBytes", info.sizeInBytes,
                "lineCount", info.lineCount
        ));
    }

    // Clear file (use with caution)
    @DeleteMapping("/clear-file")
    //@PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> clearFile(@RequestParam(required = false) boolean confirm) {
        if (!confirm) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Please confirm by adding ?confirm=true"
            ));
        }

        try {
            lawExportService.clearDataFile();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Data file cleared"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}
