package org.example.smartlawgt.query.documents;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {
    @Id
    private String _id;
    private UUID userId;
    private String email;
    private String userName;
    private String name;
    private String avatarUrlText;
    private LocalDate birthday;
    private boolean isActive;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
