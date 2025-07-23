package org.example.smartlawgt.command.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "law_type")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LawTypeEntity {

    @Id
    @Column(name = "law_type_id")
    private UUID lawTypeId;

    @Column(name = "name", columnDefinition = "nvarchar(max)")
    private String name;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "lawType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LawEntity> laws;
}
