package org.example.smartlawgt.integration.export.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartlawgt.integration.ai.services.CustomDataService;
import org.example.smartlawgt.query.documents.LawDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.domain.Sort;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
@RequiredArgsConstructor
@Slf4j
public class LawExportService {
    private final MongoTemplate mongoTemplate;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    @Autowired
    private CustomDataService customDataService;
    // Fixed file path - always append to this file
    private static final String DATA_FILE_PATH = "src/main/resources/data.txt";

    /**
     * Append all valid laws to existing data.txt file
     */
    public String appendAllLaws() {
        try {
            ensureFileExists();
            Query query = Query.query(Criteria.where("status").is("VALID"))
                    .with(Sort.by(Sort.Direction.ASC, "lawNumber"));
            List<LawDocument> laws = mongoTemplate.find(query, LawDocument.class);

            appendToFile(laws);

            // Notify cache about data append
            customDataService.onDataAppended();

            log.info("Successfully appended {} laws to {}", laws.size(), DATA_FILE_PATH);
            return DATA_FILE_PATH;
        } catch (Exception e) {
            log.error("Error appending laws", e);
            throw new RuntimeException("Failed to append laws: " + e.getMessage());
        }
    }

    /**
     * Append laws by type to data.txt
     */
    public String appendLawsByType(String lawTypeName) {
        try {
            ensureFileExists();
            Query query = Query.query(
                    Criteria.where("lawTypeName").is(lawTypeName)
                            .and("status").is("VALID")
            ).with(Sort.by(Sort.Direction.ASC, "effectiveDate"));

            List<LawDocument> laws = mongoTemplate.find(query, LawDocument.class);
            appendToFileWithTypeHeader(laws, lawTypeName);

            // Notify cache about data append
            customDataService.onDataAppended();

            log.info("Successfully appended {} {} laws", laws.size(), lawTypeName);
            return DATA_FILE_PATH;
        } catch (Exception e) {
            log.error("Error appending laws by type", e);
            throw new RuntimeException("Failed to append laws: " + e.getMessage());
        }
    }

    /**
     * Append single law to data.txt
     */
    public String appendSingleLaw(String lawId) {
        try {
            ensureFileExists();
            Query query = Query.query(Criteria.where("lawId").is(lawId));
            LawDocument law = mongoTemplate.findOne(query, LawDocument.class);

            if (law != null) {
                appendSingleLawToFile(law);

                // Notify cache about data append
                customDataService.onDataAppended();

                log.info("Successfully appended law: {}", law.getLawNumber());
            }
            return DATA_FILE_PATH;
        } catch (Exception e) {
            log.error("Error appending single law", e);
            throw new RuntimeException("Failed to append law: " + e.getMessage());
        }
    }

    /**
     * Clear data.txt file (optional - use with caution)
     */
    public void clearDataFile() {
        try {
            Files.write(Paths.get(DATA_FILE_PATH), "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

            // Notify cache about complete data rebuild
            customDataService.onDataRebuilt();

            log.info("Data file cleared and cache updated");
        } catch (IOException e) {
            log.error("Error clearing data file", e);
            throw new RuntimeException("Failed to clear file: " + e.getMessage());
        }
    }
    /**
     * Get current file size and line count
     */
    public FileInfo getFileInfo() {
        try {
            Path path = Paths.get(DATA_FILE_PATH);
            long size = Files.size(path);
            long lines = Files.lines(path).count();

            return new FileInfo(DATA_FILE_PATH, size, lines);
        } catch (IOException e) {
            log.error("Error getting file info", e);
            return new FileInfo(DATA_FILE_PATH, 0, 0);
        }
    }

    // ================ Private Helper Methods ================

    private void ensureFileExists() throws IOException {
        Path path = Paths.get(DATA_FILE_PATH);
        if (!Files.exists(path)) {
            Files.createFile(path);
            log.info("Created new data.txt file");
        }
    }

    private void appendToFile(List<LawDocument> laws) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(DATA_FILE_PATH, true), // true = append mode
                        StandardCharsets.UTF_8
                )
        )) {
            // Add section separator
            writer.newLine();
            writer.write("="  .repeat(60));
            writer.newLine();
            writer.write("=== THÊM NGÀY: " + LocalDateTime.now().format(DATETIME_FORMATTER) + " ===");
            writer.newLine();
            writer.write("="  .repeat(60));
            writer.newLine();
            writer.newLine();

            // Append each law
            for (int i = 0; i < laws.size(); i++) {
                writer.write(formatLawToText(laws.get(i)));
                writer.newLine();

                if (i < laws.size() - 1) {
                    writer.newLine();
                }
            }

            // Add summary
            writer.newLine();
            writer.write(String.format("--- Đã thêm %d luật ---", laws.size()));
            writer.newLine();
        }
    }

    private void appendToFileWithTypeHeader(List<LawDocument> laws, String typeName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(DATA_FILE_PATH, true),
                        StandardCharsets.UTF_8
                )
        )) {
            // Type section header
            writer.newLine();
            writer.write("="  .repeat(60));
            writer.newLine();
            writer.write("=== LOẠI LUẬT: " + typeName.toUpperCase() + " ===");
            writer.newLine();
            writer.write("=== THÊM NGÀY: " + LocalDateTime.now().format(DATETIME_FORMATTER) + " ===");
            writer.newLine();
            writer.write("="  .repeat(60));
            writer.newLine();
            writer.newLine();

            // Append laws
            for (LawDocument law : laws) {
                writer.write(formatLawToText(law));
                writer.newLine();
                writer.newLine();
            }
        }
    }

    private void appendSingleLawToFile(LawDocument law) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(DATA_FILE_PATH, true),
                        StandardCharsets.UTF_8
                )
        )) {
            writer.newLine();
            writer.write("--- Thêm lúc: " + LocalDateTime.now().format(DATETIME_FORMATTER) + " ---");
            writer.newLine();
            writer.write(formatLawToText(law));
            writer.newLine();
        }
    }

    private String formatLawToText(LawDocument law) {
        StringBuilder sb = new StringBuilder();

        sb.append("=== BẮT ĐẦU LUẬT ===\n");
        sb.append("Số hiệu: ").append(law.getLawNumber()).append("\n");
        sb.append("Tên luật: ").append(law.getDescription() != null ? law.getDescription() : "N/A").append("\n");
        sb.append("Loại: ").append(law.getLawTypeName()).append("\n");

        if (law.getIssueDate() != null) {
            sb.append("Ngày ban hành: ").append(law.getIssueDate().format(DATE_FORMATTER)).append("\n");
        }

        sb.append("Ngày hiệu lực: ").append(law.getEffectiveDate().format(DATE_FORMATTER)).append("\n");

        if (law.getExpiryDate() != null) {
            sb.append("Ngày hết hiệu lực: ").append(law.getExpiryDate().format(DATE_FORMATTER)).append("\n");
        }

        sb.append("Cơ quan ban hành: ").append(law.getIssuingBody()).append("\n");
        sb.append("\nNội dung chính:\n");

        if (law.getDescription() != null && !law.getDescription().isEmpty()) {
            sb.append(law.getDescription()).append("\n");
        }

        if (law.getContentUrl() != null && !law.getContentUrl().isEmpty()) {
            sb.append("Link tài liệu: ").append(law.getContentUrl()).append("\n");
        }

        sb.append("=== KẾT THÚC LUẬT ===");

        return sb.toString();
    }

    // Inner class for file info
    public static class FileInfo {
        public final String path;
        public final long sizeInBytes;
        public final long lineCount;

        public FileInfo(String path, long sizeInBytes, long lineCount) {
            this.path = path;
            this.sizeInBytes = sizeInBytes;
            this.lineCount = lineCount;
        }

        public String getFormattedSize() {
            if (sizeInBytes < 1024) return sizeInBytes + " B";
            if (sizeInBytes < 1024 * 1024) return String.format("%.2f KB", sizeInBytes / 1024.0);
            return String.format("%.2f MB", sizeInBytes / (1024.0 * 1024));
        }
    }


}