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
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "law_types")
public class LawTypeDocument {
    @Id
    private String lawTypeId;
    private String lawTypename;
    private Boolean isDeleted;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
