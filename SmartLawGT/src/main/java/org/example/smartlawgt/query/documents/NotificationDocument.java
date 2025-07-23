package org.example.smartlawgt.query.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notifications")
public class NotificationDocument {
    @Id
    private String notificationId;
    private String userId;
    private String title;
    private String content;
    private LocalDateTime created;
    private boolean isRead;
    private boolean isEnable;
}
