package org.example.smartlawgt.query.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "chat_histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryDocument {
    @Id
    private String id;
    private UUID userId;
    private String question;
    private String answer;
    private LocalDateTime timestamp;
}
