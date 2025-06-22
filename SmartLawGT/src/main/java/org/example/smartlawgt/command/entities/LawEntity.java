package org.example.smartlawgt.command.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "laws")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LawEntity {

    @Id
    @Column(name = "law_id")
    private UUID lawId;

    @ManyToOne
    @JoinColumn(name = "law_type_id")
    private LawTypeEntity lawType;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private UserEntity createdBy;

    @Column(name = "law_number", length = 50)
    private String lawNumber;

    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LawStatus status;

    @Column(name = "issuing_body", length = 60)
    private String issuingBody;

    @Column(name = "content_url", columnDefinition = "text")
    private String contentUrl;

    @Column(name = "description", columnDefinition = "text")
    private String description;
}
