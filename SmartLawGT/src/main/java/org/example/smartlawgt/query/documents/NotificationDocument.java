package org.example.smartlawgt.query.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notifications")
public class NotificationDocument {
    @Id
    private String id;
    private String notificationId;
    private String userId;
    private String title;
    private String content;
    private LocalDateTime created;
    private boolean read;
    private boolean enable;
}
