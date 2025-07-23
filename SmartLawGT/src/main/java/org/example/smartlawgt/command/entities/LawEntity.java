package org.example.smartlawgt.command.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "laws")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LawEntity {

    @Id
    @Column(name = "law_id")
    private String lawId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "law_type_id", nullable = false)
    private LawTypeEntity lawType;

    @Column(name = "law_number", nullable = false, columnDefinition = "nvarchar(60)")
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    private UserEntity createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "issuing_body", columnDefinition = "nvarchar(max)")
    private String issuingBody;

    @Column(name = "content_url", columnDefinition = "nvarchar(max)")
    private String contentUrl;

    @Column(name = "description", columnDefinition = "nvarchar(max)")
    private String description;
}
